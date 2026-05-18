import { ref } from 'vue'
import { defineStore } from 'pinia'

function make2DArray<T>(x: number, y: number, default_: T): T[][] {
  return Array(y).fill(default_)
    .map(()=>(
      Array(x).fill(default_)
    )
  )
}

interface Vec2D {
  x: number,
  y: number
}

export const useGameStore = defineStore('game', () => {
  const opponent = ref<string | undefined>(undefined)
  const myColor = ref<string | undefined>(undefined)
  const myTurn = ref<boolean | undefined>(undefined)
  const board = ref<string[][]>(make2DArray(8,8,'empty'))
  const grabbing = ref<string | undefined>(undefined)
  const grabbedFrom = ref<Vec2D | undefined>(undefined)
  const gameId = ref<number | undefined>(undefined)

  function grabFigure(x: number, y: number) {
    const old = board.value[y][x];
    if (old == 'empty') {
      grabbing.value = undefined
    } else {
      grabbedFrom.value = {y, x};
      grabbing.value = old;
      board.value[y][x] = 'empty';
    }
  }

  function putFigure(x: number, y: number) {
    const old = board.value[y][x]
    if (old == 'empty') {
      if (grabbedFrom.value && grabbing.value) {
        board.value[y][x] = grabbing.value;
        grabbedFrom.value = undefined;
        grabbing.value = undefined;
      }
    } else {
      if (grabbedFrom.value && grabbing.value) {
        const { x: ox, y: oy } = grabbedFrom.value;
        board.value[oy][ox] = grabbing.value;
        grabbedFrom.value = undefined;
        grabbing.value = undefined;
      }
    }
  }

  function returnFigure() {
    if (grabbedFrom.value && grabbing.value) {
      const { x: ox, y: oy } = grabbedFrom.value;
      board.value[oy][ox] = grabbing.value;
      grabbedFrom.value = undefined;
      grabbing.value = undefined;
    }
  }

  function moveFigure(from: Vec2D, to: Vec2D) {
    const rowFrom = board.value[from.y]
    const rowTo = board.value[to.y]
    if (!rowFrom || !rowTo) return

    const piece = rowFrom[from.x]
    if (piece === undefined) return

    // ensure target index exists
    if (to.x < 0 || to.x >= rowTo.length || from.x < 0 || from.x >= rowFrom.length) return

    rowTo[to.x] = piece
    rowFrom[from.x] = 'empty'
  }

  function setFigure(to: Vec2D, figure: string) {
    board.value[to.y][to.x] = figure;
  }

  return {
    opponent,
    myColor,
    myTurn,
    board,
    grabbing,
    grabbedFrom,
    setFigure,
    // moveFigure,
    grabFigure,
    putFigure,
    gameId
  }
})
