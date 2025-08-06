//file za pomoćne funkcije

const { randomInt } = require("crypto")
const {lobbies, users} = require("../serverState/serverState")

function generateLobbyId() {
  const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  let id = "";
  for (let i = 0; i < 4; i++) {
    id += chars.charAt(randomInt(0, chars.length));
  }
  return id;
}

function generateLevel(lobbyId) {
  let gameState = lobbies[lobbyId].gameState

  if(!gameState){
        gameState = {
        lobbyId: lobbyId,
        players: Array.from(lobbies[lobbyId].users_in_lobby).map(user => ({
          id: user.id,
          name: user.name,
          cards_in_hand : [],
          thrown_card : 0, //TODO fix thrown card netreba valjda bit u players? idk šta sm ja tu radio lol
        })),
        dealt_cards : [],
        level : 1,
        numberOfLives: 5
      };
      lobbies[lobbyId].gameState = gameState
    }

       else{ //čisti karte od prošlog levela bato
         gameState.dealt_cards = []
         gameState.players.forEach(player => (player.cards_in_hand = []));
         gameState.thrownCard = 0
       }

        let number;

        for(const player of gameState.players){
          for(let i = 0; i < gameState.level; i++){
            do{
              number = randomInt(1,100)
            }
            while(gameState.dealt_cards.includes(number))
            player.cards_in_hand.push(number)
            gameState.dealt_cards.push(number)
          }

          player.cards_in_hand = player.cards_in_hand.sort((a,b) => a - b) //sortiraj ih sljeva nadesno 
          gameState.dealt_cards = gameState.dealt_cards.sort((a,b) => a - b)

          lobbies[lobbyId].users_in_lobby.forEach(user =>{
            const player = gameState.players.find(p => p.id === user.id)
            if (player){
              user.send(JSON.stringify({ 
                type: "round_generated", 
                cards: player.cards_in_hand,
                level: gameState.level,
                lives: gameState.numberOfLives}));
            }
          })

        }
  }

module.exports = {
  generateLobbyId,
  generateLevel
};