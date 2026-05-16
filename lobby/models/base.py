from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import MetaData
from sqlalchemy.ext.asyncio import create_async_engine, async_sessionmaker, AsyncSession
from config import DATABASE_URL
target_metadata = MetaData()
Base = declarative_base(metadata=target_metadata)

async_engine = create_async_engine(DATABASE_URL, echo=True) #type: ignore

async_session_maker =async_sessionmaker(async_engine, expire_on_commit=False, class_=AsyncSession)