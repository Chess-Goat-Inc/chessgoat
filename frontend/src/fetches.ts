const HOST_URL = 'http://localhost:8000'


interface LoginData {
  username: string
  password: string
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
