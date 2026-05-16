from collections.abc import AsyncGenerator
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import MetaData
from sqlalchemy.ext.asyncio import create_async_engine, async_sessionmaker, AsyncSession
from config import DATABASE_URL
target_metadata = MetaData()
Base = declarative_base(metadata=target_metadata)

async_engine = create_async_engine(DATABASE_URL, echo=True) #type: ignore

async_session_maker =async_sessionmaker(async_engine, expire_on_commit=False, class_=AsyncSession)

from sqlalchemy import Column, Integer, String, Boolean, DateTime, ForeignKey


class RefreshToken(Base):
    __tablename__ = 'refresh_tokens'

    refresh_token_id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(Integer, ForeignKey('users.user_id'), nullable=False)
    refresh_token_hash = Column(String(128), nullable=False)
    is_revoked = Column(Boolean, default=False, nullable=False)
    expires_at = Column(DateTime, nullable=False)

class User(Base):
    __tablename__ = 'users'

    user_id = Column(Integer, primary_key=True, autoincrement=True)
    username = Column(String(50), unique=True, nullable=False)
    password_hash = Column(String(128), nullable=False)
    score = Column(Integer, default=1000)

async def get_async_db() -> AsyncGenerator[AsyncSession, None]:
    """
    returns async session of SQLAlchemy for working with Postgres.
    """
    async with async_session_maker() as session:

        yield session