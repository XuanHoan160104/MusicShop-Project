<%--
    File: /admin/chat.jsp
    Trang quản lý Chat cho Admin
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Admin - Quản lý Chat</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <style>
        body { font-size: .875rem; }
        .sidebar { position: fixed; top: 0; bottom: 0; left: 0; z-index: 100; padding: 48px 0 0; box-shadow: inset -1px 0 0 rgba(0, 0, 0, .1); }
        main { padding-top: 1rem; }
        .admin-sidebar .nav-link { font-weight: 500; color: #333; }
        .admin-sidebar .nav-link.active { color: #004a99; }
        .admin-sidebar .nav-link:hover { color: #f58220; }
        .chat-container {
            display: flex;
            height: calc(100vh - 200px);
            border: 1px solid #dee2e6;
            border-radius: 5px;
            overflow: hidden;
        }
        .users-list {
            width: 300px;
            border-right: 1px solid #dee2e6;
            overflow-y: auto;
            background: #f8f9fa;
        }
        .chat-messages {
            flex: 1;
            display: flex;
            flex-direction: column;
        }
        .messages-area {
            flex: 1;
            overflow-y: auto;
            padding: 15px;
            background: #f8f9fa;
        }
        .message {
            margin-bottom: 15px;
            display: flex;
            flex-direction: column;
        }
        .message.user {
            align-items: flex-start;
        }
        .message.admin {
            align-items: flex-end;
        }
        .message-content {
            max-width: 70%;
            padding: 10px 15px;
            border-radius: 10px;
            word-wrap: break-word;
        }
        .message.user .message-content {
            background: white;
            color: #333;
            border: 1px solid #dee2e6;
        }
        .message.admin .message-content {
            background: #007bff;
            color: white;
        }
        .message-time {
            font-size: 11px;
            color: #6c757d;
            margin-top: 5px;
        }
        .input-area {
            padding: 15px;
            border-top: 1px solid #dee2e6;
            background: white;
        }
        .user-item {
            padding: 12px 15px;
            border-bottom: 1px solid #dee2e6;
            cursor: pointer;
            transition: background 0.2s;
        }
        .user-item:hover {
            background: #e9ecef;
        }
        .user-item.active {
            background: #007bff;
            color: white;
        }
        .user-item.unread {
            font-weight: bold;
        }
    </style>
</head>
<body>
    <jsp:include page="header_admin.jsp"></jsp:include>
    
    <div class="container-fluid">
        <div class="row">
            <jsp:include page="sidebar_admin.jsp"></jsp:include>
            
            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1 class="h2"><i class="fas fa-comments me-2"></i>Quản lý Chat</h1>
                    <c:if test="${not empty requestScope.unreadCount && requestScope.unreadCount > 0}">
                        <span class="badge bg-danger">
                            <i class="fas fa-bell me-1"></i>${requestScope.unreadCount} tin nhắn chưa đọc
                        </span>
                    </c:if>
                </div>
                
                <c:choose>
                    <c:when test="${empty requestScope.usersWithMessages || requestScope.usersWithMessages.size() == 0}">
                        <div class="alert alert-info">
                            <i class="fas fa-info-circle me-2"></i>Chưa có tin nhắn nào từ người dùng.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="chat-container">
                            <div class="users-list">
                                <div class="p-2 bg-primary text-white">
                                    <strong><i class="fas fa-users me-2"></i>Danh sách người dùng</strong>
                                </div>
                                <c:forEach items="${requestScope.usersWithMessages}" var="user">
                                    <div class="user-item ${requestScope.selectedUser != null && requestScope.selectedUser.user_id == user.user_id ? 'active' : ''}" 
                                         onclick="loadChat(${user.user_id})">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div>
                                                <strong>${user.full_name != null ? user.full_name : user.username}</strong>
                                                <br><small>${user.username}</small>
                                            </div>
                                            <i class="fas fa-chevron-right"></i>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                            
                            <div class="chat-messages">
                                <c:if test="${not empty requestScope.selectedUser}">
                                    <div class="p-3 bg-primary text-white">
                                        <strong><i class="fas fa-user me-2"></i>Chat với: ${requestScope.selectedUser.full_name != null ? requestScope.selectedUser.full_name : requestScope.selectedUser.username}</strong>
                                        <br><small>Email: ${requestScope.selectedUser.email}</small>
                                    </div>
                                    
                                    <div class="messages-area" id="messagesArea">
                                        <c:forEach items="${requestScope.messages}" var="msg">
                                            <div class="message ${msg.is_from_admin ? 'admin' : 'user'}">
                                                <div class="message-content">${msg.message}</div>
                                                <div class="message-time">
                                                    <fmt:formatDate value="${msg.created_at}" pattern="dd-MM-yyyy HH:mm:ss"/>
                                                    <c:if test="${msg.product_name != null}">
                                                        <br><small><i class="fas fa-box me-1"></i>Sản phẩm: ${msg.product_name}</small>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                    
                                    <div class="input-area">
                                        <form id="chatForm" onsubmit="sendMessage(event)">
                                            <input type="hidden" id="userId" value="${requestScope.selectedUser.user_id}">
                                            <div class="input-group">
                                                <input type="text" id="messageInput" class="form-control" placeholder="Nhập tin nhắn..." required>
                                                <button type="submit" class="btn btn-primary">
                                                    <i class="fas fa-paper-plane me-2"></i>Gửi
                                                </button>
                                            </div>
                                        </form>
                                    </div>
                                </c:if>
                                <c:if test="${empty requestScope.selectedUser}">
                                    <div class="d-flex align-items-center justify-content-center h-100 text-muted">
                                        <div class="text-center">
                                            <i class="fas fa-comments fa-3x mb-3"></i>
                                            <p>Chọn một người dùng để xem tin nhắn</p>
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </main>
        </div>
    </div>
    
    <jsp:include page="footer_admin.jsp"></jsp:include>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Khôi phục tin nhắn đã lưu khi trang load
        <c:if test="${not empty requestScope.selectedUser}">
        (function() {
            const userId = ${requestScope.selectedUser.user_id};
            const savedMessage = localStorage.getItem('admin_draft_message_' + userId);
            if (savedMessage) {
                const messageInput = document.getElementById('messageInput');
                if (messageInput) {
                    messageInput.value = savedMessage;
                }
            }
        })();
        </c:if>
        
        function loadChat(userId) {
            // Lưu tin nhắn đang nhập trước khi chuyển user
            const messageInput = document.getElementById('messageInput');
            if (messageInput) {
                const currentUserId = document.getElementById('userId');
                if (currentUserId && currentUserId.value) {
                    const currentMessage = messageInput.value;
                    if (currentMessage.trim()) {
                        localStorage.setItem('admin_draft_message_' + currentUserId.value, currentMessage);
                    } else {
                        localStorage.removeItem('admin_draft_message_' + currentUserId.value);
                    }
                }
            }
            window.location.href = 'chat?user_id=' + userId;
        }
        
        function sendMessage(event) {
            event.preventDefault();
            const userId = document.getElementById('userId').value;
            const messageInput = document.getElementById('messageInput');
            const message = messageInput.value.trim();
            
            if (!message) {
                alert('Vui lòng nhập nội dung tin nhắn!');
                return;
            }
            
            // Lưu tin nhắn vào biến để hiển thị ngay
            const messageText = message;
            
            // Sử dụng URLSearchParams để gửi data như form-urlencoded
            const params = new URLSearchParams();
            params.append('message', message);
            params.append('user_id', userId);
            
            console.log('Admin sending message:', message);
            console.log('Admin sending to user_id:', userId);
            
            fetch('${pageContext.request.contextPath}/admin/chat', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                },
                body: params.toString()
            })
            .then(response => {
                console.log('Admin response status:', response.status);
                if (!response.ok) {
                    throw new Error('HTTP error! status: ' + response.status);
                }
                return response.text().then(text => {
                    try {
                        return JSON.parse(text);
                    } catch (e) {
                        console.error('Failed to parse JSON:', text);
                        throw new Error('Invalid JSON response');
                    }
                });
            })
            .then(data => {
                console.log('Admin response data:', data);
                if (data.success) {
                    // Xóa input và xóa tin nhắn đã lưu trong localStorage
                    messageInput.value = '';
                    localStorage.removeItem('admin_draft_message_' + userId);
                    
                    // Thêm tin nhắn mới vào DOM ngay lập tức (không refresh)
                    const messagesArea = document.getElementById('messagesArea');
                    if (messagesArea) {
                        const messageDiv = document.createElement('div');
                        messageDiv.className = 'message admin';
                        
                        const now = new Date();
                        const timeStr = now.toLocaleString('vi-VN', {
                            day: '2-digit',
                            month: '2-digit',
                            year: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit',
                            second: '2-digit'
                        });
                        
                        // Tạo message-content div
                        const messageContent = document.createElement('div');
                        messageContent.className = 'message-content';
                        messageContent.textContent = messageText;
                        
                        // Tạo message-time div
                        const messageTime = document.createElement('div');
                        messageTime.className = 'message-time';
                        messageTime.textContent = timeStr;
                        
                        // Thêm vào messageDiv
                        messageDiv.appendChild(messageContent);
                        messageDiv.appendChild(messageTime);
                        
                        // Thêm vào messagesArea
                        messagesArea.appendChild(messageDiv);
                        messagesArea.scrollTop = messagesArea.scrollHeight;
                    }
                } else {
                    alert('Lỗi: ' + (data.message || 'Không xác định'));
                }
            })
            .catch(error => {
                console.error('Error sending message:', error);
                alert('Có lỗi xảy ra khi gửi tin nhắn! ' + error.message);
            });
        }
        
        // Lưu tin nhắn đang nhập vào localStorage mỗi khi gõ
        <c:if test="${not empty requestScope.selectedUser}">
        const messageInput = document.getElementById('messageInput');
        const userId = document.getElementById('userId').value;
        if (messageInput && userId) {
            messageInput.addEventListener('input', function() {
                const message = this.value;
                if (message.trim()) {
                    localStorage.setItem('admin_draft_message_' + userId, message);
                } else {
                    localStorage.removeItem('admin_draft_message_' + userId);
                }
            });
            
            // Lưu khi người dùng rời khỏi trang (beforeunload)
            window.addEventListener('beforeunload', function() {
                const message = messageInput.value;
                if (message.trim()) {
                    localStorage.setItem('admin_draft_message_' + userId, message);
                }
            });
        }
        </c:if>
        
        // Auto scroll to bottom
        const messagesArea = document.getElementById('messagesArea');
        if (messagesArea) {
            messagesArea.scrollTop = messagesArea.scrollHeight;
        }
        
        // Auto refresh mỗi 5 giây (nhưng không reload nếu đang nhập tin nhắn)
        <c:if test="${not empty requestScope.selectedUser}">
        let autoRefreshInterval;
        const messageInputForRefresh = document.getElementById('messageInput');
        const userIdForRefresh = document.getElementById('userId').value;
        
        // Chỉ auto refresh nếu không có focus vào input (người dùng không đang nhập)
        function shouldAutoRefresh() {
            return document.activeElement !== messageInputForRefresh && 
                   (!messageInputForRefresh || messageInputForRefresh.value.trim() === '');
        }
        
        autoRefreshInterval = setInterval(() => {
            if (shouldAutoRefresh()) {
                // Chỉ reload nếu không đang nhập
                const currentMessage = messageInputForRefresh ? messageInputForRefresh.value : '';
                if (currentMessage.trim()) {
                    localStorage.setItem('admin_draft_message_' + userIdForRefresh, currentMessage);
                }
                loadChat(${requestScope.selectedUser.user_id});
            }
        }, 5000);
        </c:if>
    </script>
</body>
</html>

