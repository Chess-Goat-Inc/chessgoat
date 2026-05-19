import os
from cryptography.hazmat.primitives import serialization

from dotenv import load_dotenv

load_dotenv()

DATABASE_URL = os.getenv("DATABASE_URL")
SECRET_KEY_REFRESH = os.getenv("SECRET_KEY_REFRESH")
ALGORITH = os.getenv("ALGORITHM")

with open('jwt_public.pem', 'rb') as key_file:
    key = serialization.load_pem_public_key(
        key_file.read()
    )
SECRET_KEY_ACCESS = key
REDIS_HOST = os.getenv("REDIS_HOST")
