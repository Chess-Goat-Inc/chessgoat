FROM python:alpine3.22

WORKDIR /app

COPY config.py requirements.txt ./

RUN pip install -r ./requirements.txt && \
    pip install uvicorn[standard]

COPY . .

CMD [ "python3", "-m", "main"]