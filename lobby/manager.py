from fastapi import FastAPI, WebSocket, WebSocketDisconnect, status
import asyncio


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


manager = ConnetctionManager()