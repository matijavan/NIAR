const { gameMessages } = require("../routers/gameRouter")
const { lobbyMessages } = require("../routers/lobbyRouter")
const { mainMessages } = require("../routers/mainRouter")
const { selectNameMessages } = require("../routers/selectNameRouter")

function handleMessage(ws, data, wss){
    if (data.type in selectNameMessages) {
      selectNameMessages[data.type](ws, data, wss);
      } 
    else if (data.type in mainMessages) {
        mainMessages[data.type](ws, data, wss);
      } 
    else if (data.type in lobbyMessages) {
        lobbyMessages[data.type](ws, data, wss);
      } 
    else if (data.type in gameMessages) {
        gameMessages[data.type](ws, data, wss);
      } 
    else {
        ws.send(JSON.stringify({ type: "error", message: "Unknown message recieved from client" }));
      }
}

module.exports = {handleMessage}
