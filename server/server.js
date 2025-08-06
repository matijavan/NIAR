const { randomInt } = require("crypto");
const express = require("express");
const http = require("http");
const WebSocket = require("ws");
const app = express();
const server = http.createServer(app);
const { v4: uuidv4 } = require('uuid');
const { count } = require("console");
const { handleMessage } = require("./handlers/handler")

const wss = new WebSocket.Server({ server });

//TODO: možda da uradimo konekciju tek kada korisnik unese željeni nickname?
wss.on("connection", (ws) => {
  
  ws.id = uuidv4() //neki generator ID-eva neka magija, možda ni ne treba al nez neka ga
  console.log("novi korisnik se spojio na server", ws.id)

  ws.on("message", (msg) => {
    try{
      const data = JSON.parse(msg) 
      handleMessage(ws, data, wss)
    } catch(e){
      console.error("KUME NEŠTO SI ZA GRDO ZAKUHO\n", e) 
    }
    
  });

  ws.on("close", () => {
    console.log("klijent se disconnectovao");
  });

});

app.get("/", (req, res) => {
  res.send("WebSocket server is running");
});

server.listen(3000, () => {
  console.log("Server running on http://localhost:3000");
});

