from passlib.context import CryptContext
from fastapi.security import OAuth2PasswordBearer, HTTPBearer
from datetime import datetime, timedelta, timezone
import hashlib
import jwt
from sqlalchemy import select
from config import SECRET_KEY_ACCESS, ALGORITH, SECRET_KEY_REFRESH
from typing import Any

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/auth/login")

ACESS_TOKEN_EXPIRE_MINUTES = 30
REFRESH_TOKEN_EXPIRE_DAYS = 15

TEST_EXPIRE_ACCESS = 10
def hash_password(password: str) -> str:
    return pwd_context.hash(password)


def verify_password(plain_password: str, hashed_password: str) -> bool:
    return pwd_context.verify(plain_password, hashed_password)


def create_access_token(data: dict) -> str:
    to_encode = data.copy()
    expire = datetime.now(timezone.utc) + timedelta(minutes=ACESS_TOKEN_EXPIRE_MINUTES)
    
    # expire = datetime.now(timezone.utc) + timedelta(seconds=TEST_EXPIRE_ACCESS)
    to_encode.update({"exp": expire})
    return jwt.encode(to_encode, SECRET_KEY_ACCESS, ALGORITH) #type: ignore


def hash_ref_token(token: str) -> str:
    return hashlib.sha256(token.encode()).hexdigest()

def create_refresh_token(data: dict) -> dict[str, Any]:
    to_encode = data.copy()
    expire = datetime.now() + timedelta(days=REFRESH_TOKEN_EXPIRE_DAYS)
    to_encode.update({"exp": expire})
    return {"token": jwt.encode(to_encode, SECRET_KEY_REFRESH, ALGORITH), "expire": expire} #type: ignore


