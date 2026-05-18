
export function start_game_websocket(game_id: number) {
  const URI = `ws://localhost:6767/game?id=${game_id}`
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
