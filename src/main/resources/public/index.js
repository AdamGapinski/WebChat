var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/index/");
webSocket.onmessage = handleServerMessage;
webSocket.onopen = promptUsername;
setEventHandlers();

function handleServerMessage(message) {
    var messageDataJson = JSON.parse(message.data);
    switch (messageDataJson.type) {
        case "channel":
            showChannel(messageDataJson);
            break;
    }
}

function showChannel(data) {
    id("channels").insertAdjacentHTML("afterbegin", data.content);
}

function promptUsername() {
    var user = getCookie("username");
    while (user == null || user === "") {
        user = prompt("Please enter your username: ", "username");
    }
    setCookie("username", user);
    id("username").insertAdjacentHTML("afterbegin", "<b>" + user + "</b>");
}

function setEventHandlers() {
    id("newChannelButton").addEventListener("click", function () {
        var channel = prompt("Type channel name: ");
        if (channel != null && channel != "") {
            webSocket.send(channel)
        }
    });
}

function id(id) {
    return document.getElementById(id);
}

function getCookie(cookieName) {
    var name = cookieName + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function setCookie(name, value) {
    document.cookie = name + "=" + value;
}
