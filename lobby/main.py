from fastapi import FastAPI, WebSocket, WebSocketDisconnect, status
import asyncio
app = FastAPI()

class ConnetctionManager:
    def __init__(self) -> None:
        self.active_connections: dict[str, WebSocket] = {}

    def connect(self, wb: WebSocket, token: str):
        '''
        Add connection to the list in the ConnectionManager
        '''
        token_fake = {"name": "oleg"}
        username = token_fake.get("name")
        self.active_connections[username] = wb #type: ignore
    
    def disconnect(self, username: str):
        '''
        Remove connection fron the list in the ConnectionManager
        '''
        self.active_connections.pop(username)
    
    async def send_challenge_request(): pass

    async def accept_challenge(): pass

    async def decline_challenge(): pass

class UserManager:
    fake_db: dict[str, str] = {}

    async def get_by_id(): pass

    async def get_by_username(): pass

    

manager = ConnetctionManager()

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
