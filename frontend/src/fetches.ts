import type { Player } from "./models"
import { EMPTY_PLAYER } from "./models"

const LOBBY_HOST_URL = 'http://localhost:8000'
const AUTH_HOST_URL = 'http://localhost:8080'


interface LoginData {
  username: string
  password: string
}


export async function fetch_players(offset: number = 0, limit: number = 100): Promise<Player[]> {
  const params = {offset: String(offset), limit: String(limit)};
  const headers = new Headers({
    'Content-Type': 'application/json'
  })
  const response = await fetch(
    `${LOBBY_HOST_URL}/users/?` + new URLSearchParams(params).toString(),
    { method: 'GET', headers: headers }
  )
  if (!response.ok) {
    console.log('Fetch error:', response);
    return [];
  }
  const body = await response.json()
  const players: Player[] = [];
  console.log(body);
  body.forEach(u => {
    u.is_me = false;
    u.score = u.rating;
    delete u.rating;
    delete u.id;
    players.push(u);
  });
  console.log(players);
  return players;
}

export async function fetch_player(username: string): Promise<Player> {
  const headers = new Headers({
    'Content-Type': 'application/json'
  })
  const response = await fetch(
    `${LOBBY_HOST_URL}/users/${username}`,
    { method: 'GET', headers: headers }
  )
  if (!response.ok) {
    console.log('Fetch error:', response)
    return EMPTY_PLAYER;
  }
  const body = await response.json();
  body.is_me = false;
  body.score = body.rating;
  delete body.rating;
  delete body.id;
  return body;
}


export function test_ws() {
  const URI = 'ws://localhost:8000/lobby'
  const ws = new WebSocket(URI);
  ws.addEventListener('open', () => {
    console.log('CONNECTED');
    let counter = 0;
    setInterval(() => {
      console.log(`SENT: ping ${counter}`);
      ws.send('ping');
      counter++;
    }, 1000);
  });
}


export async function fetch_refresh(): Promise<string> {
  const headers = new Headers({
    'Content-Type': 'application/json'
  })
  const response = await fetch(
    `${AUTH_HOST_URL}/auth/refresh`, {
      method: 'GET',
      headers: headers,
      credentials: 'include',  // to get refresh token as HTTP-Only cookie
      redirect: 'follow'
    }
  )
  if (response.ok) {
    const body = await response.json();
    return body.access_token;
  } else {
    console.log('fail:', response);
    throw Error('fetch_refresh failed');
  }
}


export async function fetch_login(data: LoginData): Promise<string> {
  const headers = new Headers({
    'Content-Type': 'application/json'
  })
  const response = await fetch(
    `${AUTH_HOST_URL}/auth/login`, {
      method: 'POST',
      headers: headers,
      credentials: 'include',
      body: JSON.stringify(data)
    }
  )
  if (response.ok) {
    const body = await response.json();
    return body.access_token;
  } else {
    console.log('fail:', response);
    throw Error('fetch_login failed');
  }
}


export async function fetch_register(data: LoginData): Promise<string> {
  const headers = new Headers({
    'Content-Type': 'application/json'
  })
  const response = await fetch(
    `${AUTH_HOST_URL}/auth/register`, {
      method: 'POST',
      headers: headers,
      credentials: 'include',
      body: JSON.stringify(data)
    }
  )
  if (response.ok) {
    const body = await response.json();
    return body.access_token;
  } else {
    console.log('fail:', response);
    throw Error('fetch_register failed');
  }
}

