import { ref } from 'vue'
import { defineStore } from 'pinia'
import { useProfileStore } from './profile'
import { useAuthStore } from './auth'


// function make2DArray<T>(x: number, y: number, default_: T): T[][] {
//   return Array(y).fill(default_)
//     .map(()=>(
//       Array(x).fill(default_)
//     )
//   )
// }


export const useGameStore = defineStore('game', () => {
  const opponent = ref<string | null>(null)
  const myColor = ref<string | null>(null)
  const myTurn = ref<boolean | null>(null)

  const board = ref<string[]>(Array(64).fill('empty'))

  const grabbing = ref<string | null>(null)
  const grabbedFrom = ref<number | null>(null)

  const gameId = ref<number | null>(null)
  const ws = ref<WebSocket | null>(null)


  function initBoard() {
    board.value = Array(64).fill('empty')
    _setFigure(0, 'b_rook');
    _setFigure(1, 'b_knight');
    _setFigure(2, 'b_bishop');
    _setFigure(3, 'b_queen');
    _setFigure(4, 'b_king');
    _setFigure(5, 'b_bishop');
    _setFigure(6, 'b_knight');
    _setFigure(7, 'b_rook');
    _setFigure(8, 'b_pawn');
    _setFigure(9, 'b_pawn');
    _setFigure(10, 'b_pawn');
    _setFigure(11, 'b_pawn');
    _setFigure(12, 'b_pawn');
    _setFigure(13, 'b_pawn');
    _setFigure(14, 'b_pawn');
    _setFigure(15, 'b_pawn');

    _setFigure(48, 'w_pawn');
    _setFigure(49, 'w_pawn');
    _setFigure(50, 'w_pawn');
    _setFigure(51, 'w_pawn');
    _setFigure(52, 'w_pawn');
    _setFigure(53, 'w_pawn');
    _setFigure(54, 'w_pawn');
    _setFigure(55, 'w_pawn');
    _setFigure(56, 'w_rook');
    _setFigure(57, 'w_knight');
    _setFigure(58, 'w_bishop');
    _setFigure(59, 'w_queen');
    _setFigure(60, 'w_king');
    _setFigure(61, 'w_bishop');
    _setFigure(62, 'w_knight');
    _setFigure(63, 'w_rook');
  }


  function grabFigure(idx: number) {
    const old = board.value[idx]
    if (old === undefined) {
      return
    }
    else if (old === 'empty') {
      grabbing.value = null
    }
    else {
      grabbedFrom.value = idx;
      grabbing.value = old;
      board.value[idx] = 'empty';
    }
  }

  function canPut(idx: number): boolean {
    const old = board.value[idx]
    if (old === undefined) return false
    else return true
  }

  function putFigure(idx: number): boolean {
    const old = board.value[idx]
    if (old === undefined) {
      return false
    }
    else if (old === 'empty') {
      _putGrabbedTo(idx)
      return true
    }
    else if (grabbing.value && _isOppositeColor(grabbing.value, old)) {
      _putGrabbedTo(idx)
      return true
    }
    else {
      _putGrabbedBack()
      return true
    }
  }

  function _putGrabbedTo(idx: number) {
    if (grabbedFrom.value && grabbing.value) {
      board.value[idx] = grabbing.value
      grabbedFrom.value = null
      grabbing.value = null
    }
  }

  function _putGrabbedBack() {
    if (grabbedFrom.value && grabbing.value) {
      const from = grabbedFrom.value
      board.value[from] = grabbing.value
      grabbedFrom.value = null
      grabbing.value = null
    }
  }

  function _isOppositeColor(fig1: string, fig2: string) {
    return fig1[0] != fig2[0];
  }

  function _moveFigure(from: number, to: number) {
    const fromFigure = board.value[from];
    const toFigure = board.value[to];
    if (fromFigure === undefined || fromFigure === 'empty') {
      return
    }
    if (toFigure === undefined) {
      return
    }
    board.value[from] = 'empty';
    board.value[to] = fromFigure;
  }

  function _setFigure(idx: number, figure: string) {
    board.value[idx] = figure;
  }

  function start_game_websocket(game_id: number) {
    console.log('reconnecting')
    const URI = `ws://localhost:6767/game?id=${game_id}`
    ws.value = new WebSocket(URI);

    const auth = useAuthStore()

    ws.value.addEventListener('open', () => {
      console.log('CONNECTED');
      const token = auth.access_token
      if (ws.value)
        ws.value.send(`${token}`)
    });
    ws.value.addEventListener('message', (e)=> {
      const message = JSON.parse(e.data)
      switch (message.type) {
        case 'move': {
          const m = message;
          _moveFigure(m.from, m.to)
        }
      }
      console.log(e.data)
    })
    ws.value.addEventListener('error', (event) => {
      console.log('WebSocket error:', event)
    })
    ws.value.addEventListener('close', (event) => {
      console.log('WebSocket closed:', event.code, event.reason);
    });
  }

  return {
    gameId,
    ws,
    opponent,
    myColor,
    myTurn,
    board,
    grabbing,
    grabbedFrom,
    initBoard,
    _setFigure,
    _moveFigure,
    grabFigure,
    canPut,
    putFigure,
    _putGrabbedTo,
    _putGrabbedBack,
    start_game_websocket,
  }
})
