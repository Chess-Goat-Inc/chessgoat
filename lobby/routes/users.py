from fastapi import APIRouter, status, HTTPException, Header, Depends
from pydantic import BaseModel, Field, field_validator
from enum import Enum
from .dependences import get_async_db
from fastapi.security import OAuth2PasswordBearer

from typing import Optional

from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from sqlalchemy import select, text, func, desc, case, union
from lobby.models.users import User as UserModel
from lobby.models.game import Game as GameModel
from lobby.manager import manager

router = APIRouter(prefix="/users")
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")

class User(BaseModel):
    id: int 
    username: str = Field(..., min_length=3)
    rating: int
    place: int
    status: str

    @field_validator("status")
    @classmethod
    def check_status(cls, value, info):
        if value not in ["online", "offline", "in-game"]:
            raise HTTPException(status_code=status.HTTP_422_UNPROCESSABLE_CONTENT, detail="There is no such status")
        return value

@router.get("/me", response_model=User)
async def get_current_user(token: str = Depends(oauth2_scheme), session: AsyncSession = Depends(get_async_db)):
    username = "alice" #TODO add decode
    subqry = (
        select(
            UserModel.user_id,
            UserModel.username,
            UserModel.score,
            func.row_num().over(order_by=desc(UserModel.score).label("place"))
        ).subquery()
    )
    stmt = select(subqry).where(UserModel.username == username)
    result = await session.scalars(stmt)
    user = result.first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return User(
        id=user.user_id,
        username=user.username,
        rating=user.score,
        place=user.place + 1,
        status="online"
    )

@router.get("/{username}", response_model=User)
async def get_user_by_username(username: str, session: AsyncSession = Depends(get_async_db)):
    white_players = select(GameModel.white_id.label("player_id")).where(GameModel.state == "started")
    black_players = select(GameModel.black_id.label("player_id")).where(GameModel.state == "started")
    active_users_cte = union(white_players, black_players).cte("active_users")

    subqry = (
        select(
            UserModel.user_id,
            UserModel.username,
            UserModel.score,
            func.row_number().over(order_by=desc(UserModel.score)).label("place"),
            case(
                (UserModel.user_id.in_(select(active_users_cte.c.player_id)), "in-game"),
                else_="offline"
            ).label("status")
        ).subquery()
    )
    
    stmt = select(
        subqry.c.user_id,
        subqry.c.username,
        subqry.c.score,
        subqry.c.place,
        subqry.c.status
    ).where(subqry.c.username == username).where(UserModel.username == username)
    result = await session.execute(stmt)
    user = result.first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return User(
        id=user.user_id,
        username=user.username,
        rating=user.score,
        place=user.place,
        status=user.status if user.status == "in-game" or manager.active_connections.get(user.username) == None else "online"
    )

@router.get("/", response_model=list[User])
async def get_users(offset: int = 0, limit: int = 0, session: AsyncSession = Depends(get_async_db)):
    raw_sql = text("""
        WITH active_users AS (
            SELECT white_id AS player_id FROM games WHERE state = 'started'
            UNION
            SELECT black_id FROM games WHERE state = 'started'
        )
        SELECT 
            user_id,
            username,
            score,
            CASE 
                WHEN user_id IN (SELECT player_id FROM active_users) THEN 'in-game'
                ELSE 'offline'
            END AS status
        FROM users
        ORDER BY score DESC
        LIMIT :limit OFFSET :offset;
    """)
    result = await session.execute(
        raw_sql, 
        {"limit": limit, "offset": offset}
        )
    users = result.fetchall()
    
    return [User(
        id=u.user_id,
        username=u.username,
        rating=u.score,
        place=i+1+offset,
        status=u.status if u.status == "in-game" or manager.active_connections.get(u.username) == None else "online"
    ) for i, u in enumerate(users)]

