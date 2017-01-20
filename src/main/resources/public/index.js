var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/index/");
webSocket.onmessage = function (msg) { receiveMessage(msg); };
webSocket.onclose = function () { alert("WebSocket connection closed") };

var userName = setUserName("Please enter your username:");

id("newChannelButton").addEventListener("click", function () {
    var channel = prompt("Wpisz nazwę kanału: ");
    if (channel !== '' && channel !== null) {
        sendMessage(channel);
    }
});

function sendMessage(message) {
    if (message !== "") {
        webSocket.send(message);
    }
}

function checkUserExists(data) {
    data.usernames.forEach(function (other) {
        if (getCookie("username") === other) {
            setUserName(other + "is not available username. Please enter other username: ");
        }
    })
}
function receiveMessage(msg) {
    var data = JSON.parse(msg.data);
    var type = data.type;

    if (type === "usernames"); {
        checkUserExists(data);
    }
    if (type == "channels") {
        updateChannelList(msg);
    }
}

function updateChannelList(msg) {
    var data = JSON.parse(msg.data);
    id("channels").innerHTML = "";
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

function setUserName(promptMsg) {
    var user = getCookie("username");
    if (user === "") {
        user = prompt(promptMsg, "username");
        if (user != "" && user != null) {
            sendMessage("username:" + user);
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

