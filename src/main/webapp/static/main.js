
var stompClient = null;
var usernameElement = document.querySelector('#username');
var messageForm = document.querySelector('#messageForm');
var connectButton = document.querySelector('#connectButton');
var recipientInput = document.querySelector('#recipient');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var notConnectedText = document.querySelector('.not-connected');
var connectedText = document.querySelector('.connected');
var username = null;

function connect() {
    var socket = new SockJS('/messages');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}
function onConnected(connection) {
    username = connection.headers['user-name'];
    usernameElement.innerText = username;
    notConnectedText.classList.add('hidden');
    connectedText.classList.remove('hidden');
    connectButton.classList.add('hidden');

    stompClient.subscribe('/user/queue/message', onMessageReceived);
}

function onError(error) {
    notConnectedText.classList.remove('hidden');
    connectedText.classList.add('hidden');
    connectButton.classList.remove('hidden');
    alert(error);
}

function sendMessage(event) {
    var recipient = recipientInput.value.trim();
    var messageContent = messageInput.value.trim();

    if(recipient && messageContent && stompClient) {
        stompClient.send("/app/message." + recipient, {}, messageContent);
    }

    addMessage({
        sender: 'me',
        recipient: recipient,
        content: messageContent,
        date: new Date()
    }, true);

    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    console.log('message received: ', message);
    addMessage(message)
}

function addMessage(message, self) {

    var messageElement = document.createElement('li');
    messageElement.classList.add('chat-message');
    if(self) {
        messageElement.classList.add('self')
    }

    var user = document.createElement('span');
    user.classList.add('badge', 'badge-success');
    user.innerText = message.sender;

    var content = document.createElement('span');
    content.classList.add('content');
    content.innerText = message.content;

    var date = document.createElement('div');
    date.classList.add('date');
    date.innerText = new Date(message.date).toLocaleString();

    messageElement.appendChild(user);
    messageElement.appendChild(content);
    messageElement.appendChild(date);
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

connect();
connectButton.addEventListener('click', connect);
messageForm.addEventListener('submit', sendMessage, true);