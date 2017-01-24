var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/channel");
webSocket.onmessage = function (msg) { updateChat(msg); };
webSocket.onopen = function () { sendMessage(saveToChannelJson()) }


id("send").addEventListener("click", function () {
    sendMessage(userMessageToJson(id("message").value));
    id("message").value = "";
});


id("leavechannel").addEventListener("click", function () {
    webSocket.close();
    window.location.replace("http://" + location.hostname + ":" + location.port);
});

id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) {
        sendMessage(userMessageToJson(e.target.value));
        id("message").value = "";
    }
});

function userMessageToJson(message) {
    if (message !== "" && message !== null) {
        return {
            type : "message",
            username : getCookie("username"),
            channel : getParameterByName("channel"),
            text : message,
            datetime : Date.now()
        }
    }
}

function saveToChannelJson() {
    return {
        type: "channeluser",
        channel : getParameterByName("channel"),
        username : getCookie("username")
    }
}

function sendMessage(message) {
    webSocket.send(JSON.stringify(message));
}

function updateChat(msg) {
    var data = JSON.parse(msg.data);
    insert("chat", data.message);
    id("userlist").innerHTML = "";
    new Set(data.userlist).forEach(function (user) {
        if (user !== getCookie("username")) {
            insert("userlist", "<li>" + user + "</li>");
        }
    })

    insert("userlist", "<b><li>" + getCookie("username") + "</li></b>");

    ;
}

function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
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
    url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}