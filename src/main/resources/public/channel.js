var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/channel");
webSocket.onmessage = handleServerMessage;
webSocket.onopen = handleClientConnection;
setEventHandlers();

function handleServerMessage (message) {
    var messageDataJson = JSON.parse(message.data);
    switch (messageDataJson.type) {
        case "message":
            showMessage(messageDataJson);
            break;
        case "userJoined":
            showUser(messageDataJson);
            break;
        case "userLeft":
            removeUser(messageDataJson);
    }
}

function handleClientConnection () {
    sendObjectToServer({
        type: "connection",
        channel : getParameterByName("channel"),
        username : getCookie("username")
    });
    id("channelname").insertAdjacentHTML("afterbegin", getParameterByName("channel"));
}

function showMessage(data) {
    id("chat").insertAdjacentHTML("afterbegin", data.message);
}

function showUser(data) {
    if (data.currentUser == "true") {
        id("userlist").insertAdjacentHTML("afterbegin", "<li class=\"list-group-item\"><b>" + data.username + "</b></li>");
    } else {
        id("userlist").insertAdjacentHTML("beforeend", "<li class=\"list-group-item\">" + data.username + "</li>");
    }
}

function removeUser(data) {
    var userList = id("userlist").innerHTML;
    id("userlist").innerHTML = userList.replace("<li class=\"list-group-item\">" + data.username + "</li>", "");
}

function sendMessageToServer(message) {
    if (message !== null && message !== "") {
        var jsonMessage = {
            type: "message",
            username: getCookie("username"),
            channel: getParameterByName("channel"),
            content: message,
            datetime: Date.now()
        };
        sendObjectToServer(jsonMessage);
        id("message").value = "";
    }
}

function sendObjectToServer(object) {
    webSocket.send(JSON.stringify(object))
}

function setEventHandlers() {
    id("send").addEventListener("click", function () {
        sendMessageToServer(id("message").value);
    });

    id("leavechannel").addEventListener("click", function () {
        webSocket.close(1000);
        window.location.replace("http://" + location.hostname + ":" + location.port);
    });

    id("message").addEventListener("keypress", function (e) {
        if (e.keyCode === 13) {
            sendMessageToServer(e.target.value);
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

function getParameterByName(name) {
    var url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}