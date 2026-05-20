# Chess Goat
### Startup Guide
1. Clone the repository to desired directory
```shell
git clone https://github.com/Chess-Goat-Inc/chessgoat
cd chessgoat
```
2. Create the `.env` file in the root of the project, with the following contents:
```env
DATABASE_URL = postgresql+asyncpg://postgres:postgres@db:5432/chess_goat

POSTGRES_USER = postgres
POSTGRES_DB = chess_goat
POSTGRES_PASSWORD = postgres

REDIS_HOST = redis

PRIVATE_KEY_FILE = /app/secrets/jwt_private.pem
AUTH_CORS_ADDRS = http://localhost:5173
ALGORITHM = "RS256"
```
3. Generate a pair of keys (private/public) for authentication use
```bash
openssl genpkey -algorithm RSA -out jwt_private.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in jwt_private.pem -out jwt_public.pem
```
4. Put private/public key files in the following directives
```shell
# in the root of directories
mkdir ./auth/secrets
cp jwt_private.pem jwt_public.pem auth/secrets
cp jwt_public.pem lobby
cp jwt_public.pem mock_game
```
5. Run docker compose
```shell
# that will setup all the backend services
docker compose up --build
```
6. Setup the frontend
```shell
cd frontend
npm install
npm run dev
```
