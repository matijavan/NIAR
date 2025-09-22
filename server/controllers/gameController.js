const {generateLevel} = require("../utils/utils");
const {lobbies} = require("../serverState/serverState")

function throwCard(ws){
      lobbyId = ws.lobby
      const gameState = lobbies[lobbyId].gameState
      let player = gameState.players.find(p => p.id === ws.id)
      let karte = player.cards_in_hand
      let bacena_karta = karte[0]
      let karta_koja_treba_biti_bacena = gameState.dealt_cards[0]

      if(!bacena_karta){
        ws.send(JSON.stringify({
          type: "you_threw_all_cards"
        }))
      }
      
      else if(bacena_karta === karta_koja_treba_biti_bacena){
        correctCardIsThrown(ws, lobbies, lobbyId, gameState, player, karte, bacena_karta)
      }

      else{
        wrongCardIsThrown(ws, lobbies, lobbyId, gameState, player, karte,
           bacena_karta, karta_koja_treba_biti_bacena)
      }
    }

function correctCardIsThrown(ws, lobbies, lobbyId, gameState, player, karte, bacena_karta){
  gameState.dealt_cards.shift()
  player.cards_in_hand.shift()

  ws.send(JSON.stringify({
    type: "thrown_card_is_correct", 
    card : bacena_karta, 
    cards: player.cards_in_hand})
  ) 

  lobbies[lobbyId].users_in_lobby.forEach(user => { //šalji update ostalim userima (netreba im update deck al treba update thrown_card )
    if (user.id !== ws.id) {
      user.send(JSON.stringify({
        type: "other_player_threw",
        card: bacena_karta,
        player: ws.name //ovo da drugim uesrima javim koji igrac je bacio trebace za feature
      }));
    }
  });

  if(gameState.dealt_cards.length === 0){ //ako su sve karte bačene(i to ispravno!), gg prešo si level
    lobbies[lobbyId].users_in_lobby.forEach(user => {
      user.send(JSON.stringify({
        type: "level_passed",
        level : gameState.level,
        lives: gameState.lives
      }))
      //console.log("preso si level kume") RADI AJMOOOOO
    })
    gameState.level++
    generateLevel(lobbyId)
  }
}

function wrongCardIsThrown(ws, lobbies, lobbyId, gameState, player, karte,
   bacena_karta, karta_koja_treba_biti_bacena){
    gameState.numberOfLives--;
    let playerWhoHadCorrectCardInHand =  gameState.players.find(p =>
    p.cards_in_hand[0] === gameState.dealt_cards[0])

    console.log(player.name, "je bacio kartu", bacena_karta)
    console.log(playerWhoHadCorrectCardInHand.name, "je imao kartu", gameState.dealt_cards[0])

    lobbies[lobbyId].users_in_lobby.forEach(user =>{
    user.send(JSON.stringify({
      type: "thrown_card_is_wrong", 
      thrownCard : bacena_karta, 
      shouldBeThrownCard: karta_koja_treba_biti_bacena, 
      numberOfLives : gameState.numberOfLives, 
      playerWhoThrewWrongCard: player.name, 
      playerWhoHadCorrectCardInHand: playerWhoHadCorrectCardInHand.name
      }))
    })
    generateLevel(lobbyId)
    }
    

module.exports = {
  throwCard
};
