from fastapi import FastAPI, WebSocket, WebSocketDisconnect, status
import asyncio

from lobby.routes.challenges import router as challenge_router
from lobby.routes.users import router as user_router
import uvicorn
from lobby.manager import manager
app = FastAPI()
app.include_router(user_router)
app.include_router(challenge_router)


@app.websocket("/lobby")
async def websocket_endpoint(websocket: WebSocket):
    await websocket.accept()
    try:
        token = await asyncio.wait_for(
                websocket.receive_text(),
                timeout=15.0
        )
        # token.check
        manager.connect(websocket, token)

    except asyncio.TimeoutError:
        await websocket.close()

    except WebSocketDisconnect:
        print("User disconnects")

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)