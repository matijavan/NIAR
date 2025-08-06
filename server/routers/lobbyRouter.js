const lobbyController = require("../controllers/lobbyController")

const lobbyMessages = {
    ready: lobbyController.ready,
    unready: lobbyController.unready,
    leave_lobby: lobbyController.leaveLobby
}

module.exports = { lobbyMessages };