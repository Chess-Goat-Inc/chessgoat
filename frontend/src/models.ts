export interface LoginData {
  username: string
  password: string
}


export interface Player {
  place: number,
  score: number,
  username: string,
  status: string,
  is_me: boolean,
}


export const EMPTY_PLAYER: Player = {
  place: -1,
  score: 0,
  username: 'John_IDK_Doe',
  status: 'offline',
  is_me: false
}

export interface Opponent {
  id: number,
  username: string
}
export interface RequestMessage {
  type: string,
  opponent: Opponent
  requested_at: number
}