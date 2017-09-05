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
socket.onmessage = function (event) {
    var json = JSON.parse(event.data);
    var message = new Message(json.text, json.sender, json.date);
    printMessage(message);
};

//отправить сообщение
function sendMessage() {
    var text = document.getElementById("input").value;
    if (text.length > 0) {
        var message = new Message(text, "test", new Date);
        printMessage(message);
        document.getElementById("input").value = "";
        socket.send(JSON.stringify(message));
    }
}

//вывод сообщения
function printMessage(message) {
    console.log(message.toString());
    document.getElementById("messages").innerHTML += message.toString();
}

//запрос на загрузку истории сообщений
function loadMessages() {
    socket.send(LOAD_MESSAGES_COMMAND);
}

//конструктор сообщения
function Message(text, sender, date) {
    this.text = text;
    this.sender = sender;
    this.date = date;
    this.toString = function () {
        var newMessage = messageHTML.replace("#", this.text);
        newMessage = newMessage.replace("@", this.date.toLocaleString("ru"));
        return newMessage.split("*").join(false ? "myMessage" : "friendsMessage");
    };
}