from fastapi import FastAPI, WebSocket, WebSocketDisconnect, status, HTTPException
import asyncio
import jwt
from red1s import r
from sqlalchemy.future import select

class ConnetctionManager:
    TIMEOUT = 60
    def __init__(self) -> None:
        self.active_connections: dict[str, WebSocket] = {}
        self.challenges: dict[int, asyncio.Task] = {}
        
    def is_online(self, nickname: str):
        '''
        Checks if user online and returns its connection, returns false otherwise
        '''
        wb = self.active_connections.get(nickname)
        return wb if wb else False

    def connect(self, wb: WebSocket, username: str):
        '''
        Add connection to the list in the ConnectionManager
        '''
        self.active_connections[username] = wb #type: ignore
    
    def disconnect(self, username: str):
        '''
        Remove connection fron the list in the ConnectionManager
        '''
        self.active_connections.pop(username)
    
    async def _start_timer_challenge(self, challenge_id: int):
        try:
            await asyncio.sleep(self.TIMEOUT)

        except asyncio.CancelledError:
            print(f'Challenge {challenge_id} was accepted or canceled')
        
        finally:
            self.challenges.pop(challenge_id, None)
            r.delete(f"challenge:{challenge_id}")


    def create_timer(self, challenge_id: int):
        task = asyncio.create_task(self._start_timer_challenge(challenge_id))
        self.challenges[challenge_id] = task

    def cancel_timer(self, challenge_id: int):
        task = self.challenges.get(challenge_id)
        if task:
            task.cancel()

    async def accept_challenge(): pass

    async def decline_challenge(): pass


manager = ConnetctionManager()