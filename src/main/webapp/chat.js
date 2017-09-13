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
    if(event.data.indexOf("Users count:") === 0) {
        changeOnlineUsersCount(event.data.substr(12));
    } else {
        var json = JSON.parse(event.data, function (key, value) {
            if (key === "date") {
                return new Date(value);
            }
            return value;
        });
        var message = new Message(json.text, json.sender, json.date);
        printMessage(message);
        scrollMessages();
    }
};

//Отображение количество пользователей онлайн
function changeOnlineUsersCount(count) {
    var text = "Сейчас здесь ";
    if(count == 0) {
        text += ("никого нет.");
    } else if(count == 1) {
        text += ("только вы :)");
    } else if(count > 1 && count < 5) {
        text += (count + " пользователя.")
    } else {
        text += (count + " пользователей.")
    }
    document.getElementById("usersCount").innerText = text;
}

//Плавная прокрутка чата к последнему сообщению.
//Скорость меняется в зависимости от длинны прокрутки.
function scrollMessages() {
    var height = document.getElementById("messages").scrollHeight;
    var currentPosition = document.getElementById("messages").scrollTop;
    var diff = height - messagesBlockHeight - currentPosition;
    if(diff > 0) {
        if(diff > 1000) {
            currentPosition += 5;
        } else if(diff > 800) {
            currentPosition += 4;
        } else if(diff > 500) {
            currentPosition += 3;
        } else if(diff > 300) {
            currentPosition += 2;
        } else if(diff > 200) {
            currentPosition += 1.5;
        } else {
            currentPosition += 1;
        }
        document.getElementById("messages").scrollTop = currentPosition;
        setTimeout(scrollMessages, 4);
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