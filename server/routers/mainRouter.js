const mainController = require("../controllers/mainController");

const mainMessages = {
    create_lobby: mainController.createLobby,
    join_lobby: mainController.joinLobby
}

module.exports = { mainMessages }