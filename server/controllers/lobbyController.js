const { generateLevel } = require("../utils/utils")
const { lobbies } = require("../serverState/serverState")

function ready(ws){
    lobbyId = ws.lobby
    lobbies[lobbyId].users_that_are_ready.add(ws)
    console.log("igrac", ws.name, "se readyovao")
    console.log("broj igraca u lobbyju: ", lobbies[lobbyId].users_in_lobby.size)
    console.log("broj readyovanih igrača:", lobbies[lobbyId].users_that_are_ready.size)

    if(lobbies[lobbyId].users_in_lobby.size === lobbies[lobbyId].users_that_are_ready.size){
        lobbies[lobbyId].users_in_lobby.forEach(user =>{
            user.send(JSON.stringify({type: "all_players_ready"}))
        })
    generateLevel(lobbyId)
     
    lobbies[lobbyId].users_that_are_ready.clear(); //da ne budu readyjani na restartu
    }

    else{
        lobbies[lobbyId].users_in_lobby.forEach(user =>{
            user.send(JSON.stringify({
                type: "someone_clicked_ready",
                player: ws.name, 
                ready_count: lobbies[lobbyId].users_that_are_ready.size}))
        })
    }
}

function unready(ws){
    lobbyId = ws.lobby
    lobbies[lobbyId].users_that_are_ready.delete(ws)
    console.log("igrac", ws.name, "se unreadyovao")
    console.log("broj igraca u lobbyju: ", lobbies[lobbyId].users_in_lobby.size)
    console.log("broj readyovanih igrača:", lobbies[lobbyId].users_that_are_ready.size)
    lobbies[lobbyId].users_in_lobby.forEach(user =>{
            user.send(JSON.stringify({
                type: "someone_clicked_unready",
                player: ws.name,
                ready_count: lobbies[lobbyId].users_that_are_ready.size}))
        })
}

function leaveLobby(ws){
    lobbyId = ws.lobby
    if(lobbies[lobbyId].users_in_lobby.size === 1){
        delete lobbies[lobbyId]
        // ws.send(JSON.stringify({
        //     type: "you_left_lobby", //ovo mozda ni ne treba?
        // }))
        ws.send(JSON.stringify({ type: "you_left_lobby" }));
        console.log(`Lobby deleted: ${lobbyId}`);
    }
    else{
        lobbies[lobbyId].users_in_lobby.delete(ws)
        if(lobbies[lobbyId].users_that_are_ready.has(ws)){
            lobbies[lobbyId].users_that_are_ready.delete(ws)
        }
        ws.send(JSON.stringify({
            type: "you_left_lobby",
        }))
        const userList = Array.from(lobbies[lobbyId].users_in_lobby).map(client => client.name);
        lobbies[lobbyId].users_in_lobby.forEach(user => {
            user.send(JSON.stringify({
            type: "user_left_lobby", 
            users_in_lobby: userList, 
            ready_count: lobbies[lobbyId].users_that_are_ready.size,
            lobbyId: lobbyId}))
        });    
        console.log(`Korisnik ${ws.name} je napustio lobby ${lobbyId}`)
    }
}

module.exports = {
    ready,
    unready,
    leaveLobby
}