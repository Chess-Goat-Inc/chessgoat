from collections.abc import AsyncGenerator

from sqlalchemy.ext.asyncio import AsyncSession

from lobby.models.base import async_session_maker

  

async def get_async_db() -> AsyncGenerator[AsyncSession, None]:
    """
    returns async session of SQLAlchemy for working with Postgres.
    """
    async with async_session_maker() as session:

        yield session