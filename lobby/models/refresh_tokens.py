from sqlalchemy import Column, Integer, String, Boolean, DateTime, ForeignKey
from .base import Base

class RefreshToken(Base):
    __tablename__ = 'refresh_tokens'

    refresh_token_id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(Integer, ForeignKey('users.user_id'), nullable=False)
    refresh_token_hash = Column(String(128), nullable=False)
    is_revoked = Column(Boolean, default=False, nullable=False)
    expires_at = Column(DateTime, nullable=False)
