var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/index/");
webSocket.onmessage = function (msg) { updateChannelList(msg); };
webSocket.onclose = function () { alert("WebSocket connection closed") };

setUserName()
id("newChannelButton").addEventListener("click", function () {
    var channel = prompt("Type channel name: ");
    if (channel !== '' && channel !== null) {
        sendMessage(channel);
    }
});

function sendMessage(message) {
    if (message !== "") {
        webSocket.send(message);
    }
}

function updateChannelList(msg) {
    var data = JSON.parse(msg.data);
    data.channels.forEach(function (channel) {
        insert("channels", channel);
    });
}

function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

function id(id) {
    return document.getElementById(id);
}

function setUserName() {
    var user = getCookie("username");
    if (user === "") {
        user = prompt("Please enter your username: ", "username");
        if (user != "" && user != null) {
            setCookie("username", user);
        }
    }
    insert("username", "<b>" + user + "</b>");
    return user;
}

function setCookie(name, value) {
    document.cookie = name + "=" + value;
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

