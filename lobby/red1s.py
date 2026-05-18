import redis
from config import REDIS_HOST
print(f'----------------------------------------------{REDIS_HOST}')
r = redis.Redis(host=REDIS_HOST, port=6379, decode_responses=True) #type: ignore

