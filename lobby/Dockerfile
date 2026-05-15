FROM python:alpine3.22

WORKDIR /app/lobby

COPY config.py requirements.txt alembic.ini ./

RUN pip install -r ./requirements.txt && \
    pip install uvicorn[standard]

COPY . .

CMD [ "python3", "-m", "lobby.main"]