// Тестовый файл для подключения к вебсокетам и отправки запросов на эндпоинты challenges
// frontend/src/__tests__/challenges.ws.test.ts

import { io, Socket } from 'socket.io-client';
import axios from 'axios';

const WS_URL = 'ws://localhost:8000/lobby';
const API_URL = 'http://localhost:8000/challenge';

// Получить access token (замените на реальный способ получения токена)
async function getToken(username: string, password: string): Promise<string> {
  const res = await axios.post('http://localhost:8080/login', { username, password });
  return res.data.access_token;
}

describe('Challenge WebSocket API', () => {
  let ws: WebSocket;
  let token: string;

  beforeAll(async () => {
    token = await getToken('testuser1', 'testpassword');
    ws = new WebSocket(WS_URL);
    await new Promise<void>((resolve) => {
      ws.onopen = () => {
        ws.send(token);
        resolve();
      };
    });
  });

  afterAll(() => {
    ws.close();
  });

  test('create challenge', async () => {
    // Отправить POST запрос на создание вызова
    const res = await axios.post(`${API_URL}/request/testuser2`, {}, {
      headers: { Authorization: `Bearer ${token}` }
    });
    expect(res.data.challenge_id).toBeDefined();
  });

  test('accept challenge', async () => {
    // Отправить POST запрос на принятие вызова
    const res = await axios.post(`${API_URL}/accept/testuser1`, {}, {
      headers: { Authorization: `Bearer ${token}` }
    });
    expect(res.data.game_id).toBeDefined();
  });

  test('decline challenge', async () => {
    // Отправить POST запрос на отклонение вызова
    const res = await axios.post(`${API_URL}/decline/testuser1`, {}, {
      headers: { Authorization: `Bearer ${token}` }
    });
    expect(res.data.detail).toBeDefined();
  });
});
