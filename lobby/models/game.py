from sqlalchemy import Column, Integer, String, Enum, ForeignKey
from .base import Base
import enum


class GameState(enum.Enum):
    ready = "ready"
    started = "started"
    finished = "finished"

class Game(Base):
    __tablename__ = 'games'

    game_id = Column(Integer, primary_key=True, autoincrement=True)
    state = Column(Enum(GameState), nullable=False, default=GameState.ready)
    white_id = Column(Integer, ForeignKey('users.user_id'), nullable=False)
    black_id = Column(Integer, ForeignKey('users.user_id'), nullable=False)
    winner_id = Column(Integer, ForeignKey('users.user_id'))
