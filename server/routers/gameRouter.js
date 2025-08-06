const gameController = require("../controllers/gameController")

const gameMessages = {
    throw_card: gameController.throwCard,
}

module.exports = { gameMessages};