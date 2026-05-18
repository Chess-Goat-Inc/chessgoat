
from fastapi import APIRouter, HTTPException, status, Request, Depends
from manager import manager
from red1s import r
import time
from sqlalchemy.ext.asyncio import AsyncSession
from models.game import Game, GameState
from models.users import User as UserModel
from sqlalchemy import insert
from .users import validate_token, get_user
from .dependences import get_async_db
router = APIRouter(prefix="/challenge")


@router.post("/request/{username}")
async def create_challenge(username: str, payload = Depends(validate_token), session: AsyncSession = Depends(get_async_db)):
    opponent_ws = manager.is_online(username)
    if not opponent_ws:
        raise HTTPException(status_code=400, detail="user offline")
    chall_id = r.incr("chall:next_id")
    timestamp = int(time.time() * 1000)
    initiator = payload["username"]
    r.hset(f"challenge:{chall_id}", mapping={
        "initiator": initiator,
        "opponent": username,
        "requested_at": timestamp
    })
    manager.create_timer(chall_id)
    user = await get_user(initiator, session)
    if user is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="There is no such user")
    await opponent_ws.send_json({
        "type": "challenge_request",
        "opponent": {"id": user.id, "username": initiator},
        "requested_at": timestamp
    })
    return {"challenge_id": chall_id}

@router.post("/accept/{username}")
async def accept_challenge(username: str, session: AsyncSession = Depends(get_async_db), payload = Depends(validate_token)):
    initiator = payload["username"]
    opponent_ws = manager.is_online(username)
    if not opponent_ws:
        raise HTTPException(status_code=400, detail="user offline")
    chall_id = None
    for key in r.scan_iter("challenge:*"):
        data = r.hgetall(key)
        if data.get("initiator") == username and data.get("opponent") == initiator: #type: ignore
            chall_id = int(key.split(":")[-1])
            break
    if not chall_id:
        raise HTTPException(status_code=404, detail="Challenge not found or expired")

    game_id = r.incr("game:next_id")
    r.hset(f"game:{game_id}", mapping={
        "state": "somebulshit",
        "white": initiator,
        "black": username
    })
    black_player = await get_user(username, session)
    white_player = await get_user(initiator, session)
    if not white_player or not black_player:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="There is no such players")
    stmt = insert(Game).values(state=GameState.ready, white_id=white_player.id, black_id=black_player.id)
    await session.execute(stmt)
    await session.commit()
    
    manager.cancel_timer(chall_id)
    
    initiator_ws = manager.is_online(initiator)
    msg_to_initiator = {
        "type": "challenge_accept",
        "opponent": {"id": white_player.id, "username": username},
        "gameId": game_id
    }

    msg_to_username = {
        "type": "challenge_accept",
        "opponent": {"id": black_player.id, "username": initiator},
        "gameId": game_id
    }
    if initiator_ws:
        await initiator_ws.send_json(msg_to_initiator)
    if opponent_ws:
        await opponent_ws.send_json(msg_to_username)
    return {"game_id": game_id}

@router.post("/decline/{username}")
async def decline_challenge(username: str, payload = Depends(validate_token), session: AsyncSession = Depends(get_async_db)):
    initiator = payload["username"]
    
    opponent_ws = manager.is_online(username)
    if not opponent_ws:
        raise HTTPException(status_code=400, detail="user offline")

    chall_id = None
    for key in r.scan_iter("challenge:*"):
        data = r.hgetall(key)
        if data.get("initiator") == username and data.get("opponent") == initiator: #type: ignore
            chall_id = int(key.split(":")[-1])
            break
    if not chall_id:
        return {"detail": "Challenge not found or already expired"}
    manager.cancel_timer(chall_id)
    opponent = await get_user(initiator, session)
    if not opponent:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="There is no such opponent")
    await opponent_ws.send_json({
        "type": "challenge_decline",
        "opponent": {"id": opponent.id, "username": initiator}
    })
    return {"detail": "Challenge declined"}
    
