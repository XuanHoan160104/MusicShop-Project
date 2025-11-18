<%--
    Chat Widget - Floating button và chat box cho người dùng
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${sessionScope.account != null && sessionScope.account.role == 'customer'}">
<style>
    #chatWidget {
        position: fixed;
        bottom: 20px;
        right: 20px;
        z-index: 1000;
    }
    #chatButton {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        background-color: #007bff;
        color: white;
        border: none;
        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        position: relative;
    }
    #chatButton:hover {
        background-color: #0056b3;
    }
    #chatButton .badge {
        position: absolute;
        top: -5px;
        right: -5px;
        background-color: #dc3545;
        color: white;
        border-radius: 50%;
        width: 24px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 12px;
    }
    #chatBox {
        position: fixed;
        bottom: 90px;
        right: 20px;
        width: 350px;
        height: 500px;
        background: white;
        border-radius: 10px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        display: none;
        flex-direction: column;
        z-index: 1001;
    }
    #chatBox.active {
        display: flex;
    }
    #chatHeader {
        background: #007bff;
        color: white;
        padding: 15px;
        border-radius: 10px 10px 0 0;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    #chatMessages {
        flex: 1;
        overflow-y: auto;
        padding: 15px;
        background: #f8f9fa;
    }
    .message {
        margin-bottom: 10px;
        display: flex;
        flex-direction: column;
    }
    .message.user {
        align-items: flex-end;
    }
    .message.admin {
        align-items: flex-start;
    }
    .message-content {
        max-width: 80%;
        padding: 10px;
        border-radius: 10px;
        word-wrap: break-word;
    }
    .message.user .message-content {
        background: #007bff;
        color: white;
    }
    .message.admin .message-content {
        background: white;
        color: #333;
        border: 1px solid #dee2e6;
    }
    .message-time {
        font-size: 11px;
        color: #6c757d;
        margin-top: 5px;
    }
    #chatInputArea {
        padding: 15px;
        border-top: 1px solid #dee2e6;
        display: flex;
        gap: 10px;
    }
    #chatInput {
        flex: 1;
        border: 1px solid #ced4da;
        border-radius: 20px;
        padding: 8px 15px;
        outline: none;
    }
    #chatSendBtn {
        background: #007bff;
        color: white;
        border: none;
        border-radius: 20px;
        padding: 8px 20px;
        cursor: pointer;
    }
    #chatSendBtn:hover {
        background: #0056b3;
    }
</style>

<div id="chatWidget">
    <button id="chatButton" onclick="toggleChat()">
        <i class="fas fa-comments"></i>
        <span id="unreadBadge" class="badge" style="display: none;">0</span>
    </button>
    <div id="chatBox">
        <div id="chatHeader">
            <div>
                <strong><i class="fas fa-headset me-2"></i>Chat với Admin</strong>
                <br><small>Về sản phẩm và đơn hàng</small>
            </div>
            <button onclick="toggleChat()" style="background: none; border: none; color: white; font-size: 20px; cursor: pointer;">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <div id="chatMessages"></div>
        <div id="chatInputArea">
            <input type="text" id="chatInput" placeholder="Nhập tin nhắn..." onkeypress="handleChatKeyPress(event)">
            <button id="chatSendBtn" onclick="sendMessage()"><i class="fas fa-paper-plane"></i></button>
        </div>
    </div>
</div>

<script>
    let chatOpen = false;
    let pollInterval = null;

    function toggleChat() {
        chatOpen = !chatOpen;
        const chatBox = document.getElementById('chatBox');
        if (chatOpen) {
            chatBox.classList.add('active');
            loadMessages();
            startPolling();
        } else {
            chatBox.classList.remove('active');
            stopPolling();
        }
    }

    function loadMessages() {
        fetch('${pageContext.request.contextPath}/chat')
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const messagesDiv = document.getElementById('chatMessages');
                    messagesDiv.innerHTML = '';
                    
                    if (data.messages && data.messages.length > 0) {
                        data.messages.forEach(msg => {
                            addMessageToUI(msg);
                        });
                    } else {
                        messagesDiv.innerHTML = '<div class="text-center text-muted p-3">Chưa có tin nhắn nào. Hãy gửi tin nhắn cho chúng tôi!</div>';
                    }
                    
                    updateUnreadBadge(data.unread_count);
                    scrollToBottom();
                }
            })
            .catch(error => {
                console.error('Error loading messages:', error);
            });
    }

    function sendMessage() {
        const input = document.getElementById('chatInput');
        const message = input.value.trim();
        
        if (!message) {
            alert('Vui lòng nhập nội dung tin nhắn!');
            return;
        }
        
        // Sử dụng URLSearchParams để gửi data như form-urlencoded
        const params = new URLSearchParams();
        params.append('message', message);
        
        console.log('Sending message:', message);
        
        fetch('${pageContext.request.contextPath}/chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
            },
            body: params.toString()
        })
        .then(response => {
            console.log('Response status:', response.status);
            return response.json();
        })
        .then(data => {
            console.log('Response data:', data);
            if (data.success) {
                input.value = '';
                loadMessages();
            } else {
                alert('Lỗi: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error sending message:', error);
            alert('Có lỗi xảy ra khi gửi tin nhắn! ' + error.message);
        });
    }

    function handleChatKeyPress(event) {
        if (event.key === 'Enter') {
            sendMessage();
        }
    }

    function addMessageToUI(msg) {
        const messagesDiv = document.getElementById('chatMessages');
        const messageDiv = document.createElement('div');
        messageDiv.className = 'message ' + (msg.is_from_admin ? 'admin' : 'user');
        
        const contentDiv = document.createElement('div');
        contentDiv.className = 'message-content';
        contentDiv.textContent = msg.message;
        
        const timeDiv = document.createElement('div');
        timeDiv.className = 'message-time';
        const date = new Date(msg.created_at);
        timeDiv.textContent = date.toLocaleString('vi-VN');
        
        messageDiv.appendChild(contentDiv);
        messageDiv.appendChild(timeDiv);
        messagesDiv.appendChild(messageDiv);
    }

    function scrollToBottom() {
        const messagesDiv = document.getElementById('chatMessages');
        messagesDiv.scrollTop = messagesDiv.scrollHeight;
    }

    function updateUnreadBadge(count) {
        const badge = document.getElementById('unreadBadge');
        if (count > 0) {
            badge.textContent = count > 99 ? '99+' : count;
            badge.style.display = 'flex';
        } else {
            badge.style.display = 'none';
        }
    }

    function startPolling() {
        pollInterval = setInterval(() => {
            if (chatOpen) {
                loadMessages();
            }
        }, 3000); // Poll mỗi 3 giây
    }

    function stopPolling() {
        if (pollInterval) {
            clearInterval(pollInterval);
            pollInterval = null;
        }
    }

    // Load tin nhắn chưa đọc khi trang load
    window.addEventListener('load', function() {
        fetch('${pageContext.request.contextPath}/chat')
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    updateUnreadBadge(data.unread_count);
                }
            })
            .catch(error => console.error('Error loading unread count:', error));
    });
</script>
</c:if>

