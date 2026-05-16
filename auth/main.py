from fastapi import FastAPI, Response, APIRouter, status, HTTPException, Depends, Cookie, Request

from datetime import datetime, timedelta, timezone
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from model import get_async_db, RefreshToken as TokenModel, User as UserModel
from token_auth import REFRESH_TOKEN_EXPIRE_DAYS, verify_password, create_access_token, create_refresh_token, hash_ref_token, hash_password, SECRET_KEY_REFRESH, SECRET_KEY_ACCESS, ALGORITH
import uvicorn
from pydantic import BaseModel
import jwt


class UserInfo(BaseModel):
    username: str
    password: str

class RefreshTokenRequest(BaseModel):
    refresh_token: str | None = None


router = APIRouter(prefix="/auth", tags=["auth"])

def set_refresh_cookie(response: Response, value: str) -> None:
    response.set_cookie(
        key="refresh_token",
        value=value,
        httponly=True,
        path="/auth/refresh",
        samesite='lax',
        max_age=REFRESH_TOKEN_EXPIRE_DAYS * 24 * 60 * 60
    )

@router.post("/login", status_code=status.HTTP_201_CREATED)
async def login(response: Response, form_data: UserInfo, session: AsyncSession = Depends(get_async_db)) -> dict[str, str]:
    username = form_data.username
    password = form_data.password

    stmt = select(UserModel).where(UserModel.username == username)
    result = await session.scalars(stmt)
    user: UserModel = result.first()
    if (user is None) or (not verify_password(password, user.password_hash)): #type: ignore 
        raise HTTPException(status_code= status.HTTP_404_NOT_FOUND, detail="Username or password is not correct", headers={"WWW-Authenticate": "Bearer"})

    data = {"sub": str(user.user_id), "username": user.username}
    refresh_token_data = create_refresh_token(data)
    refresh_token = refresh_token_data["token"]
    expire = refresh_token_data["expire"]

    token_obj = TokenModel(
        user_id=user.user_id,
        refresh_token_hash=hash_ref_token(refresh_token),
        is_revoked=False,
        expires_at=expire
    )
    session.add(token_obj)
    await session.commit()

    result = {"access_token": create_access_token(data=data), "refresh_token": refresh_token}
    set_refresh_cookie(response, refresh_token)
    return result




@router.post("/refresh", status_code=status.HTTP_201_CREATED)
async def refresh_access_token(
    response: Response,
    session: AsyncSession = Depends(get_async_db),
    refresh_token: str = Cookie(None),
    body: RefreshTokenRequest|None = None
) -> dict:
    # 1. Пробуем взять из куки, если нет — из тела
    token = refresh_token or (body.refresh_token if body else None)
    if not token:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="No refresh token provided")
    try:
        payload = jwt.decode(token, SECRET_KEY_REFRESH, algorithms=[ALGORITH]) #type: ignore
        user_id = int(payload["sub"])
        db_token = await session.execute(
            select(TokenModel).where(TokenModel.user_id == user_id, TokenModel.refresh_token_hash == hash_ref_token(token), TokenModel.is_revoked == False)
        )
        db_token = db_token.scalars().first()
        if db_token is None or db_token.expires_at < datetime.now(): #type: ignore
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid or expired refresh token")
        db_token.is_revoked = True #type: ignore
        payload.pop("exp", None)
        refresh_token_data = create_refresh_token(payload)
        new_refresh_token = refresh_token_data["token"]
        expire = refresh_token_data["expire"]
        new_token_obj = TokenModel(
            user_id=user_id,
            refresh_token_hash=hash_ref_token(new_refresh_token),
            is_revoked=False,
            expires_at=expire
        )
        session.add(new_token_obj)
        await session.commit()
        result = {"access_token": create_access_token(payload), "refresh_token": new_refresh_token}
        set_refresh_cookie(response, new_refresh_token)
        return result
    except jwt.ExpiredSignatureError:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Refresh token expired")
    except (jwt.PyJWTError, jwt.DecodeError):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid refresh token")





@router.post("/register", status_code=status.HTTP_201_CREATED)
async def register(
    response: Response,
    form_data: UserInfo,
    session: AsyncSession = Depends(get_async_db)
) -> dict:
    stmt = select(UserModel).where(UserModel.username == form_data.username)
    result = await session.scalars(stmt)
    user = result.first()
    if user is not None:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="User with such username already exists")
    
    new_user = UserModel(
        username=form_data.username,
        password_hash=hash_password(form_data.password),
        score=1000
    )
    session.add(new_user)
    await session.commit()
    await session.refresh(new_user)
    
    data = {"sub": str(new_user.user_id), "username": new_user.username}
    refresh_token_data = create_refresh_token(data)
    refresh_token = refresh_token_data["token"]
    expire = refresh_token_data["expire"]
    token_obj = TokenModel(
        user_id=new_user.user_id,
        refresh_token_hash=hash_ref_token(refresh_token),
        is_revoked=False,
        expires_at=expire
    )
    session.add(token_obj)
    await session.commit()
    result = {"access_token": create_access_token(data=data), "refresh_token": refresh_token}
    set_refresh_cookie(response, refresh_token)
    return result

app = FastAPI()


app.include_router(router)
if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8001)