var messageHTML = "<div class='wrapper *'><span class='date'>@ от </span>" +
    "<span class='userName'>?</span></div><div class='message text *'>#</div>";
var socket = new WebSocket("ws://192.168.50.187:8080/chat/connect");
const LOAD_MESSAGES_COMMAND = "***LOAD#MESSAGES***";
const STOP_CHAT_COMMAND = "***STOP#CHAT***";
var userName = "";
var messagesBlockHeight = 410;

window.onload = function () {
    start();
};

window.onunload = function () {
    stopChat();
};

//Начало. Сокрытие ненужных блоков.
function start() {
    document.getElementById("chat").style.display = "none";
    document.getElementById("userName").focus();
}

//Запуск чата
function startChat() {
    userName = document.getElementById("userName").value;
    if(userName.length !== 0) {
        changeContent();
        loadMessages();
    }
}

//Закрыть чат
function stopChat() {
    socket.send(STOP_CHAT_COMMAND);
}

//Скрытие ввода и показ чата
function changeContent() {
    document.getElementById("login").classList.add("hide");
    setTimeout(function() {
        document.getElementById("login").style.display = "none";
        document.getElementById("chat").style.display = "block";
    }, 1000);
    setTimeout(function() {
        document.getElementById("chat").classList.add("show");
        document.getElementById("input").focus();
    },1200);
}

//Отправка сообщения по нажатию ENTER
document.onkeyup = function (event) {
    if (event.keyCode === 13) {
        sendMessage();
    }
};

//Обработка ответа от сервера
socket.onmessage = function (event) {
    var json = JSON.parse(event.data, function (key, value) {
        if (key === "date") {
            return new Date(value);
        }
        return value;
    });
    var message = new Message(json.text, json.sender, json.date);
    printMessage(message);
    scrollMessages();
};

//Прокрутка чата к последнему сообщению
function scrollMessages() {
    var height = document.getElementById("messages").scrollHeight;
    var currentPosition = document.getElementById("messages").scrollTop;
    if(height - messagesBlockHeight > currentPosition) {
        document.getElementById("messages").scrollTop = currentPosition + 1;
        setTimeout(scrollMessages, 10);
    }
}

//Отправить сообщение
function sendMessage() {
    var text = document.getElementById("input").value;
    if (text.length > 0) {
        var message = new Message(text, userName, new Date);
        printMessage(message);
        document.getElementById("input").value = "";
        socket.send(JSON.stringify(message));
    }
    scrollMessages();
}

//Вывод сообщения
function printMessage(message) {
    document.getElementById("messages").innerHTML += message.toString();
}

//Запрос на загрузку истории сообщений
function loadMessages() {
    socket.send(LOAD_MESSAGES_COMMAND);
}

//Конструктор сообщения
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

//Форматируем дату
function formatDate(date) {
    return formatNumber(date.getHours()) + ":" + formatNumber(date.getMinutes()) + ":" +
        formatNumber(date.getSeconds()) + " " + formatNumber(date.getDate()) + "." +
        formatNumber(date.getMonth()) + "." + date.getFullYear();
}

//Формат числа
function formatNumber(number) {
    return number.toString().length > 1 ? number : "0" + number;
}