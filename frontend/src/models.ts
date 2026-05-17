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
