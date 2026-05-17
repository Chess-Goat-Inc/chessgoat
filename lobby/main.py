from fastapi import FastAPI, WebSocket, WebSocketDisconnect, status, Depends, WebSocketException
import asyncio
from config import SECRET_KEY_ACCESS, ALGORITH
from sqlalchemy.ext.asyncio import AsyncSession
from routes.challenges import router as challenge_router
from routes.users import router as user_router
import uvicorn
import jwt
from sqlalchemy.ext.asyncio import AsyncSession
from routes.dependences import get_async_db
from manager import manager
app = FastAPI()
app.include_router(user_router)
app.include_router(challenge_router)


@app.websocket("/lobby")
async def websocket_endpoint(websocket: WebSocket, session: AsyncSession = Depends(get_async_db)):
    await websocket.accept()
    try:
        token = await asyncio.wait_for(
                websocket.receive_text(),
                timeout=15.0
        )
        try:
            payload = jwt.decode(token, SECRET_KEY_ACCESS, algorithms=[ALGORITH]) #type: ignore
        except jwt.ExpiredSignatureError:
            raise WebSocketException(code=4001, reason="Access token expired")
        except (jwt.PyJWTError, jwt.DecodeError):
            raise WebSocketException(code=4001, reason="Access token is invalid")
        username = payload["username"]
        manager.connect(websocket, username)
        while True:
            data = await websocket.receive_text()

    except asyncio.TimeoutError:
        await websocket.close()

    except WebSocketDisconnect:
        print("User disconnects")

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)