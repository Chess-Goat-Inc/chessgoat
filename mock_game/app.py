import asyncio
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.websockets import WebSocket, WebSocketDisconnect

from fastapi.exceptions import WebSocketException

import jwt

from config import SECRET_KEY_ACCESS, ALGORITH
from manager import manager

from red1s import r


app = FastAPI()


# CORS: allow frontend dev servers to make preflighted requests
origins = [
    "http://localhost:5173",
    "http://127.0.0.1:5173",
    "http://localhost:3000",
    "http://localhost:5174",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)



@app.websocket('/game')
async def game_websocket(websocket: WebSocket, id: int):
    game_id = id
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
        manager.connect(websocket, username, game_id)

        game = r.hgetall(f'game:{game_id}')
        color = 'white' if game['white'] == username else 'black'
        opp_color = 'white' if color == 'black' else 'black'
        opp_username = game[opp_color]

        while True:
            data = await websocket.receive_text()
            opp_ws = manager.active_connections.get(opp_username, None)
            print("Got opp_ws", opp_ws, flush=True)
            print(f"{color[0]} | received: {data}")
            if opp_ws:
                await opp_ws.send_text(data)
                print(f"{color[0]} | retranslated: {data}")

    except WebSocketDisconnect:
        print("User Disconnects")
