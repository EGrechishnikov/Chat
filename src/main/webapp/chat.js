var messageHTML = "<p class='date *'>@</p><div class='message text *'>#</div>";
var socket = new WebSocket("ws://localhost:8080/chat/connect");
const LOAD_MESSAGES_COMMAND = "***LOAD#MESSAGES***";

window.onload = function () {
    setTimeout(loadMessages, 500);
};

//отправка сообщения по нажатию ENTER
document.onkeyup = function (event) {
    if (event.keyCode === 13) {
        sendMessage();
    }
};

//обработка ответа от сервера
socket.onmessage = function (message) {
    printMessage(new Message(message.data, false));
};

//отправить сообщение
function sendMessage() {
    var message = document.getElementById("input").value;
    if (message.length > 0) {
        printMessage(new Message(message, true));
        document.getElementById("input").value = "";
        socket.send(message);
    }
}

//вывод сообщения
function printMessage(message) {
    document.getElementById("messages").innerHTML += message.toString();
}

//запрос на загрузку истории сообщений
function loadMessages() {
    socket.send(LOAD_MESSAGES_COMMAND);
}

//конструктор сообщения
function Message(text, isMessageMy) {
    this.toString = function () {
        var newMessage = messageHTML.replace("#", text);
        newMessage = newMessage.replace("@", new Date.toLocaleString("ru"));
        return newMessage.split("*").join(isMessageMy ? "myMessage" : "friendsMessage");
    };
}