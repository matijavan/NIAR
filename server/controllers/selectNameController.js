const {users} = require("../serverState/serverState")

function selectName(ws, data){
    ws.name = data.name
    users[ws.id] = ws.name
    console.log("ID ", ws.id, "je izabrao ime ", ws.name);
}

module.exports = {selectName}