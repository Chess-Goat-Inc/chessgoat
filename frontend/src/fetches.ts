import type { Player } from "./models"

const HOST_URL = 'http://localhost:8000'


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
    `${HOST_URL}/users/?` + new URLSearchParams(params).toString(),
    { method: 'GET', headers: headers }
  )
  if (!response.ok) {
    console.log("Fetch error:", response);
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


export async function fetch_refresh(): Promise<string> {
  const headers = new Headers({
    'Content-Type': 'application/json'
  })
  let response = await fetch(
    `${HOST_URL}/auth/refresh`, {
      method: 'GET',
      headers: headers,
      credentials: 'include',  // to get refresh token as HTTP-Only cookie
      redirect: 'follow'
    }
  )
  return '';
}


export async function fetch_login(data: LoginData): Promise<string> {
  const headers = new Headers({
    'Content-Type': 'application/json'
  })
  let response = await fetch(
    `${HOST_URL}/auth/login`, {
      method: 'POST',
      headers: headers,
      credentials: 'include',
      body: JSON.stringify(data)
    }
  )
}
