var messageHTML = "<p class='date *'>@</p><div class='message text *'>#</div>";
var socket = new WebSocket("ws://localhost:8080/chat/connect");
const LOAD_MESSAGES_COMMAND = "GET_ALL_MESSAGES";
var isMessageStoryLoaded = false;

window.onload = function() {
    loadMessages();
};

//отправка сообщения по нажатию ENTER
document.onkeyup = function (event) {
    if(event.keyCode === 13) {
        sendMessage();
    }
};

//обработка ответа от сервера
socket.onmessage = function(message) {
    if(isMessageStoryLoaded || typeof message === "string") {
        printMessage(new Message(message, false, new Date));
    } else {
        printMessages(message);
        isMessageStoryLoaded = true;
    }
};

//отправить сообщение
function sendMessage() {
    var date = new Date;
    var message = document.getElementById("input").value;
    if(message.length > 0) {
        printMessage(new Message(message, true, date));
        document.getElementById("input").value = "";
        socket.send(message);
    }
}

//вывод сообщения
function printMessage(message) {
    document.getElementById("messages").innerHTML += message.toString();
}

//вывод истории сообщений
function printMessages(messages) {
    for(var i=0; i<messages.length; i++) {
        printMessage(new Message(messages[i].message, false, new Date));
    }
}

//запрос на загрузку истории сообщений
function loadMessages() {
    socket.send(LOAD_MESSAGES_COMMAND);
}

//конструктор сообщения
function Message(text, isMessageMy, date) {
    this.text = text;
    this.isMessegeMy = isMessageMy;
    this.date = date;
    this.toString = function() {
        var newMessage = messageHTML.replace("#", text);
        newMessage = newMessage.replace("@", date.toLocaleString("ru"));
        return newMessage.split("*").join(isMessageMy ? "myMessage" : "friendsMessage");
    };
}