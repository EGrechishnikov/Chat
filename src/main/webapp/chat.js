var messageHTML = "<div class='wrapper *'><span class='date'>@ от </span>" +
    "<span class='userName'>?</span></div><div class='message text *'>#</div>";
var socket = new WebSocket("ws://192.168.1.10:8080/chat/connect");
const LOAD_MESSAGES_COMMAND = "***LOAD#MESSAGES***";
var userName = "";

window.onload = function () {
    start();
};

//начало. сокрытие ненужных блоков.
function start() {
    document.getElementById("chat").style.display = "none";
}

function startChat() {
    userName = document.getElementById("userName").value;
    if(userName.length !== 0) {
        document.getElementById("login").style.display = "none";
        document.getElementById("userName").removeAttribute("autofocus");
        document.getElementById("chat").style.display = "block";
        document.getElementById("input").setAttribute("autofocus", "");
        loadMessages();
    }
}

//отправка сообщения по нажатию ENTER
document.onkeyup = function (event) {
    if (event.keyCode === 13) {
        sendMessage();
    }
};

//обработка ответа от сервера
socket.onmessage = function (event) {
    var json = JSON.parse(event.data, function (key, value) {
        if (key === "date") {
            return new Date(value);
        }
        return value;
    });
    var message = new Message(json.text, json.sender, json.date);
    printMessage(message);
};

//отправить сообщение
function sendMessage() {
    var text = document.getElementById("input").value;
    if (text.length > 0) {
        var message = new Message(text, userName, new Date);
        printMessage(message);
        document.getElementById("input").value = "";
        socket.send(JSON.stringify(message));
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
function Message(text, sender, date) {
    this.text = text;
    this.sender = sender;
    this.date = date;
    this.toString = function () {
        var newMessage = messageHTML.replace("#", this.text);
        newMessage = newMessage.replace("@", formatDate(date));
        newMessage = newMessage.replace("?", sender);
        return newMessage.split("*")
            .join(sender.toLowerCase() === userName.toLowerCase() ? "myMessage" : "friendsMessage");
    };
}

//форматируем дату
function formatDate(date) {
    return formatNumber(date.getHours()) + ":" + formatNumber(date.getMinutes()) + ":" +
        formatNumber(date.getSeconds()) + " " + formatNumber(date.getDate()) + "." +
        formatNumber(date.getMonth()) + "." + date.getFullYear();
}

function formatNumber(number) {
    return number.toString().length > 1 ? number : "0" + number;
}