const {generateLobbyId} = require("../utils/utils")
const {lobbies} = require("../serverState/serverState")

function createLobby(ws) {
  let lobbyId;
  do {
    lobbyId = generateLobbyId();
  } while (lobbies[lobbyId]);

  lobbies[lobbyId] = {
    lobbyId,
    creatorWs: ws,
    users_in_lobby: new Set([ws]),
    users_that_are_ready: new Set(),
    gameState: null,
  };

  ws.lobby = lobbyId;
  console.log(`Lobby created: ${lobbyId}`);

  ws.send(JSON.stringify({
    type: "lobby_created",
    lobbyId: lobbyId,
    name: ws.name,
  }));
}

function joinLobby(ws, data){
    lobbyId = data.id
    ws.lobby = lobbyId //trenutni korisnik je u ws.lobby lobbyju, da nam bude lakše radit
    if(!lobbies[lobbyId]){
    ws.send(JSON.stringify({type: "lobby_does_not_exist"}))
    }
    
    else{
    lobbies[lobbyId].users_in_lobby.add(ws) //dodajemo usera u popis usera ovog lobbyja
    //SVIM USERIMA U LOBBYJU BROADCASTAMO POPIS USERA U LOBBYJU (da mozemo na frontendu updateat)
    const userList = Array.from(lobbies[lobbyId].users_in_lobby).map(client => client.name);

    lobbies[lobbyId].users_in_lobby.forEach(user => {
        user.send(JSON.stringify({
          type: "user_joined_lobby", 
          users_in_lobby: userList, 
          lobbyId: lobbyId}))
    });
    }
}

module.exports = {
    createLobby,
    joinLobby
}
