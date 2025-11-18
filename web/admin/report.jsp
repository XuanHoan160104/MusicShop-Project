<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Admin - Báo cáo Doanh thu</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">

    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <style>
        body { 
            font-size: .875rem; 
            background-color: #f8f9fa; 
        }
        main { 
            padding-top: 1rem; 
        }
        .chart-container { 
            background: white; 
            border-radius: 8px; 
            padding: 2rem; 
            box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
            margin-bottom: 2rem;
            position: relative;
        }
        canvas { 
            height: 400px !important; 
        }
        .page-header { 
            background: white;
            border-radius: 8px;
            padding: 1.5rem;
            box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
            margin-bottom: 2rem;
        }
        .auto-refresh-badge {
            position: absolute;
            top: 10px;
            right: 10px;
            font-size: 0.7rem;
        }
        .stat-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 8px;
            padding: 1.5rem;
            margin-bottom: 1rem;
        }
        .stat-card h5 {
            font-size: 0.9rem;
            opacity: 0.9;
            margin-bottom: 0.5rem;
        }
        .stat-card .number {
            font-size: 1.8rem;
            font-weight: bold;
            margin-bottom: 0;
        }
        .loading-spinner {
            display: none;
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            z-index: 100;
        }
        .chart-loading {
            position: relative;
        }
        .trend-badge {
            font-size: 0.75rem;
            padding: 0.25rem 0.5rem;
        }
    </style>
</head>
<body>

<jsp:include page="header_admin.jsp" />
<div class="container-fluid">
    <div class="row">
        <jsp:include page="sidebar_admin.jsp" />

        <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
            <div class="page-header position-relative">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center">
                    <div>
                        <h1 class="h2 mb-1">Báo cáo Doanh thu</h1>
                        <p class="text-muted mb-0">Theo dõi doanh thu 6 ngày gần nhất và 12 tháng gần nhất</p>
                    </div>
                    <div>
                        <span class="badge bg-info auto-refresh-badge" id="refreshStatus">
                            <i class="fas fa-sync-alt me-1"></i>Tự động cập nhật
                        </span>
                        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary">
                            <i class="fas fa-arrow-left me-1"></i> Quay lại Dashboard
                        </a>
                        <button class="btn btn-outline-primary ms-2" onclick="refreshData()" id="refreshBtn">
                            <i class="fas fa-redo me-1"></i>Làm mới
                        </button>
                    </div>
                </div>
            </div>

            <!-- Thống kê tổng quan -->
            <div class="row mb-4">
                <div class="col-md-4">
                    <div class="stat-card">
                        <h5><i class="fas fa-wallet me-2"></i>Doanh thu hôm nay</h5>
                        <p class="number" id="todayRevenue">0 ₫</p>
                        <span class="badge bg-success trend-badge" id="todayTrend">+0%</span>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="stat-card" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                        <h5><i class="fas fa-calendar-week me-2"></i>Doanh thu tuần</h5>
                        <p class="number" id="weekRevenue">0 ₫</p>
                        <span class="badge bg-success trend-badge" id="weekTrend">+0%</span>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="stat-card" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                        <h5><i class="fas fa-calendar-alt me-2"></i>Doanh thu tháng</h5>
                        <p class="number" id="monthRevenue">0 ₫</p>
                        <span class="badge bg-success trend-badge" id="monthTrend">+0%</span>
                    </div>
                </div>
            </div>

            <!-- Biểu đồ Doanh thu 6 ngày gần nhất -->
            <div class="chart-container">
                <div class="loading-spinner" id="weeklyLoading">
                    <div class="spinner-border text-primary"></div>
                    <p class="mt-2 text-muted">Đang tải dữ liệu...</p>
                </div>
                <h4 class="text-center mb-4 text-primary">
                    <i class="fas fa-chart-line me-2"></i>Doanh thu 6 ngày gần nhất
                </h4>
                <canvas id="weeklyRevenueChart"></canvas>
                <div class="text-center mt-3">
                    <small class="text-muted">
                        <i class="fas fa-sync-alt me-1"></i>
                        Dữ liệu được cập nhật tự động khi có đơn hàng mới
                    </small>
                </div>
            </div>

            <!-- Biểu đồ Doanh thu Tháng -->
            <div class="chart-container">
                <div class="loading-spinner" id="monthlyLoading">
                    <div class="spinner-border text-primary"></div>
                    <p class="mt-2 text-muted">Đang tải dữ liệu...</p>
                </div>
                <h4 class="text-center mb-4 text-primary">
                    <i class="fas fa-chart-bar me-2"></i>Doanh thu 12 tháng qua
                </h4>
                <canvas id="monthlyRevenueChart"></canvas>
                <div class="text-center mt-3">
                    <small class="text-muted">
                        <i class="fas fa-info-circle me-1"></i>
                        Tổng hợp doanh thu theo từng tháng
                    </small>
                </div>
            </div>

        </main>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    // Biến toàn cục
    let weeklyChart = null;
    let monthlyChart = null;
    let refreshInterval = null;
    let lastUpdateTime = null;

    // --- Dữ liệu từ Servlet ---
    // SỬA: Xử lý trường hợp dữ liệu null hoặc rỗng - đảm bảo luôn có mảng hợp lệ
    const weeklyLabels = <c:choose>
        <c:when test="${not empty weeklyLabels}">[
            <c:forEach var="label" items="${weeklyLabels}" varStatus="status">
                '<c:out value="${label}"/>'<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ]</c:when>
        <c:otherwise>[]</c:otherwise>
    </c:choose>;

    const weeklyData = <c:choose>
        <c:when test="${not empty weeklyData}">[
            <c:forEach var="val" items="${weeklyData}" varStatus="status">
                ${val}<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ]</c:when>
        <c:otherwise>[0, 0, 0, 0, 0, 0]</c:otherwise>
    </c:choose>;

    const monthlyLabels = <c:choose>
        <c:when test="${not empty monthlyLabels}">[
            <c:forEach var="label" items="${monthlyLabels}" varStatus="status">
                '<c:out value="${label}"/>'<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ]</c:when>
        <c:otherwise>[]</c:otherwise>
    </c:choose>;

    const monthlyData = <c:choose>
        <c:when test="${not empty monthlyData}">[
            <c:forEach var="val" items="${monthlyData}" varStatus="status">
                ${val != null ? val : 0}<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ]</c:when>
        <c:otherwise>[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]</c:otherwise>
    </c:choose>;

    // Dữ liệu thống kê - SỬA: Xử lý null
    const stats = {
        today: ${empty todayRevenue ? 0 : todayRevenue},
        week: ${empty weekRevenue ? 0 : weekRevenue},
        month: ${empty monthRevenue ? 0 : monthRevenue},
        todayTrend: ${empty todayTrend ? 0 : todayTrend},
        weekTrend: ${empty weekTrend ? 0 : weekTrend},
        monthTrend: ${empty monthTrend ? 0 : monthTrend}
    };

    // --- Khởi tạo trang ---
    document.addEventListener('DOMContentLoaded', function() {
        // DEBUG: Kiểm tra dữ liệu
        console.log("=== DEBUG REPORT DATA ===");
        console.log("Weekly Labels:", weeklyLabels, "Length:", weeklyLabels ? weeklyLabels.length : 0);
        console.log("Weekly Data:", weeklyData, "Length:", weeklyData ? weeklyData.length : 0);
        console.log("Monthly Labels:", monthlyLabels, "Length:", monthlyLabels ? monthlyLabels.length : 0);
        console.log("Monthly Data:", monthlyData, "Length:", monthlyData ? monthlyData.length : 0);
        console.log("Stats:", stats);
        
        // SỬA: Kiểm tra nếu có tham số updated=true thì hiển thị thông báo
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('updated') === 'true') {
            showNotification('Đơn hàng đã được cập nhật thành "Đã giao". Biểu đồ đã được làm mới!', 'success');
        }
        
        initPage();
        startAutoRefresh();
        updateLastUpdateTime();
    });
    
    // --- Hiển thị thông báo ---
    function showNotification(message, type) {
        // Tạo thông báo toast
        const notification = document.createElement('div');
        // SỬA: Dùng == thay vì === vì đây là JavaScript, không phải EL
        const alertType = (type == 'success') ? 'success' : 'info';
        const alertTitle = (type == 'success') ? 'Thành công!' : 'Thông báo';
        
        notification.className = 'alert alert-' + alertType + ' alert-dismissible fade show position-fixed';
        notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        notification.innerHTML = 
            '<strong>' + alertTitle + '</strong> ' + message +
            '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>';
        document.body.appendChild(notification);
        
        // Tự động ẩn sau 5 giây
        setTimeout(function() {
            notification.remove();
        }, 5000);
    }

    function initPage() {
        updateStats();
        renderWeeklyChart();
        renderMonthlyChart();
    }

    // --- Định dạng tiền tệ ---
    function formatCurrency(value) {
        return new Intl.NumberFormat('vi-VN', { 
            style: 'currency', 
            currency: 'VND' 
        }).format(value);
    }

    // --- Cập nhật thống kê ---
    function updateStats() {
        document.getElementById('todayRevenue').textContent = formatCurrency(stats.today);
        document.getElementById('weekRevenue').textContent = formatCurrency(stats.week);
        document.getElementById('monthRevenue').textContent = formatCurrency(stats.month);
        
        updateTrendBadge('todayTrend', stats.todayTrend);
        updateTrendBadge('weekTrend', stats.weekTrend);
        updateTrendBadge('monthTrend', stats.monthTrend);
    }

    function updateTrendBadge(elementId, trend) {
        const element = document.getElementById(elementId);
        if (trend > 0) {
            element.className = 'badge bg-success trend-badge';
            element.innerHTML = '<i class="fas fa-arrow-up me-1"></i>+' + Math.abs(trend) + '%';
        } else if (trend < 0) {
            element.className = 'badge bg-danger trend-badge';
            element.innerHTML = '<i class="fas fa-arrow-down me-1"></i>' + trend + '%';
        } else {
            element.className = 'badge bg-secondary trend-badge';
            element.innerHTML = '0%';
        }
    }

    // --- Kiểm tra dữ liệu biểu đồ ---
    // SỬA: Cho phép hiển thị biểu đồ ngay cả khi tất cả giá trị là 0
    function validateChartData(labels, data) {
        return labels && data && labels.length > 0 && data.length > 0 && 
               labels.length === data.length;
    }

    // --- Hiển thị loading ---
    function showLoading(chartType) {
        const loadingElement = document.getElementById(chartType + 'Loading');
        if (loadingElement) {
            loadingElement.style.display = 'block';
        }
    }

    function hideLoading(chartType) {
        const loadingElement = document.getElementById(chartType + 'Loading');
        if (loadingElement) {
            loadingElement.style.display = 'none';
        }
    }

    // --- Biểu đồ 6 ngày gần nhất ---
    function renderWeeklyChart() {
        const ctx = document.getElementById('weeklyRevenueChart');
        if (!ctx) {
            console.error("Không tìm thấy canvas weeklyRevenueChart");
            return;
        }

        showLoading('weekly');

        // Xóa biểu đồ cũ nếu tồn tại
        if (weeklyChart) {
            weeklyChart.destroy();
        }

        // SỬA: Đảm bảo luôn có dữ liệu, nếu không có thì tạo dữ liệu mặc định
        let labels = Array.isArray(weeklyLabels) && weeklyLabels.length > 0 ? weeklyLabels : [];
        let data = Array.isArray(weeklyData) && weeklyData.length > 0 ? weeklyData : [];
        
        console.log("Weekly Chart - Initial labels:", labels, "data:", data);
        
        // Nếu không có dữ liệu hoặc dữ liệu rỗng, tạo dữ liệu mặc định cho 6 ngày
        if (labels.length === 0 || data.length === 0 || labels.length !== data.length) {
            console.log("Tạo dữ liệu mặc định cho biểu đồ tuần");
            const today = new Date();
            labels = [];
            data = [];
            for (let i = 5; i >= 0; i--) {
                const date = new Date(today);
                date.setDate(date.getDate() - i);
                const day = String(date.getDate()).padStart(2, '0');
                const month = String(date.getMonth() + 1).padStart(2, '0');
                labels.push(day + '/' + month);
                data.push(0);
            }
        }

        console.log("Weekly Chart - Final labels:", labels, "data:", data);

        if (labels.length !== data.length) {
            console.error("Số lượng labels và data không khớp:", labels.length, "vs", data.length);
            hideLoading('weekly');
            return;
        }

        try {
            setTimeout(() => {
                console.log("Đang vẽ biểu đồ tuần với", labels.length, "điểm dữ liệu");
                console.log("Labels:", labels);
                console.log("Data:", data);
                
                // Đảm bảo canvas sẵn sàng
                if (!ctx || !ctx.getContext) {
                    console.error("Canvas không hợp lệ!");
                    hideLoading('weekly');
                    return;
                }
                
                weeklyChart = new Chart(ctx, {
                    type: 'line',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Doanh thu',
                            data: data,
                            backgroundColor: 'rgba(245, 130, 32, 0.1)',
                            borderColor: 'rgba(245, 130, 32, 1)',
                            borderWidth: 3,
                            pointBackgroundColor: 'rgba(245, 130, 32, 1)',
                            pointBorderColor: '#fff',
                            pointBorderWidth: 2,
                            pointRadius: 5,
                            pointHoverRadius: 7,
                            fill: true,
                            tension: 0.4
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        return formatCurrency(context.parsed.y);
                                    }
                                }
                            },
                            legend: {
                                display: true,
                                position: 'top',
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    callback: function(value) {
                                        return formatCurrency(value);
                                    },
                                    font: {
                                        size: 11
                                    }
                                },
                                grid: {
                                    color: 'rgba(0, 0, 0, 0.05)'
                                }
                            },
                            x: {
                                grid: {
                                    display: false
                                },
                                ticks: {
                                    font: {
                                        size: 11
                                    }
                                }
                            }
                        },
                        interaction: {
                            intersect: false,
                            mode: 'index'
                        }
                    }
                });
                hideLoading('weekly');
                console.log("Biểu đồ tuần đã được vẽ thành công!");
            }, 800);
        } catch (error) {
            console.error("Lỗi vẽ biểu đồ tuần:", error);
            console.error("Error details:", error.stack);
            if (ctx && ctx.parentElement) {
                ctx.parentElement.innerHTML = `
                    <div class="alert alert-danger text-center">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        Lỗi hiển thị biểu đồ: ${error.message}
                    </div>
                `;
            }
            hideLoading('weekly');
        }
    }

    // --- Biểu đồ Tháng ---
    function renderMonthlyChart() {
        const ctx = document.getElementById('monthlyRevenueChart');
        if (!ctx) {
            console.error("Không tìm thấy canvas monthlyRevenueChart");
            return;
        }

        showLoading('monthly');

        // Xóa biểu đồ cũ nếu tồn tại
        if (monthlyChart) {
            monthlyChart.destroy();
        }

        // SỬA: Đảm bảo luôn có dữ liệu, nếu không có thì tạo dữ liệu mặc định
        let labels = Array.isArray(monthlyLabels) && monthlyLabels.length > 0 ? monthlyLabels : [];
        let data = Array.isArray(monthlyData) && monthlyData.length > 0 ? monthlyData : [];
        
        console.log("Monthly Chart - Initial labels:", labels, "data:", data);
        
        // Nếu không có dữ liệu hoặc dữ liệu rỗng, tạo dữ liệu mặc định cho 12 tháng
        if (labels.length === 0 || data.length === 0 || labels.length !== data.length) {
            console.log("Tạo dữ liệu mặc định cho biểu đồ tháng");
            const today = new Date();
            labels = [];
            data = [];
            for (let i = 11; i >= 0; i--) {
                const date = new Date(today);
                date.setMonth(date.getMonth() - i);
                const month = String(date.getMonth() + 1).padStart(2, '0');
                const year = date.getFullYear();
                labels.push(month + '/' + year);
                data.push(0);
            }
        }

        console.log("Monthly Chart - Final labels:", labels, "data:", data);

        if (labels.length !== data.length) {
            console.error("Số lượng labels và data không khớp:", labels.length, "vs", data.length);
            hideLoading('monthly');
            return;
        }

        try {
            setTimeout(() => {
                console.log("Đang vẽ biểu đồ tháng với", labels.length, "điểm dữ liệu");
                console.log("Labels:", labels);
                console.log("Data:", data);
                
                // Đảm bảo canvas sẵn sàng
                if (!ctx || !ctx.getContext) {
                    console.error("Canvas không hợp lệ!");
                    hideLoading('monthly');
                    return;
                }
                
                monthlyChart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Doanh thu',
                            data: data,
                            backgroundColor: 'rgba(0, 74, 153, 0.7)',
                            borderColor: 'rgba(0, 74, 153, 1)',
                            borderWidth: 1,
                            borderRadius: 4,
                            borderSkipped: false,
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        return formatCurrency(context.parsed.y);
                                    }
                                }
                            },
                            legend: {
                                display: true,
                                position: 'top',
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    callback: function(value) {
                                        return formatCurrency(value);
                                    },
                                    font: {
                                        size: 11
                                    }
                                },
                                grid: {
                                    color: 'rgba(0, 0, 0, 0.05)'
                                }
                            },
                            x: {
                                grid: {
                                    display: false
                                },
                                ticks: {
                                    font: {
                                        size: 11
                                    }
                                }
                            }
                        }
                    }
                });
                hideLoading('monthly');
                console.log("Biểu đồ tháng đã được vẽ thành công!");
            }, 800);
        } catch (error) {
            console.error("Lỗi vẽ biểu đồ tháng:", error);
            console.error("Error details:", error.stack);
            if (ctx && ctx.parentElement) {
                ctx.parentElement.innerHTML = `
                    <div class="alert alert-danger text-center">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        Lỗi hiển thị biểu đồ: ${error.message}
                    </div>
                `;
            }
            hideLoading('monthly');
        }
    }

    // --- Làm mới dữ liệu ---
    function refreshData() {
        const btn = document.getElementById('refreshBtn');
        const originalHtml = btn ? btn.innerHTML : '';
        
        if (btn) {
            btn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Đang cập nhật...';
            btn.disabled = true;
        }

        // SỬA: Reload trang để lấy dữ liệu mới nhất từ server
        // Thêm timestamp để tránh cache
        const timestamp = new Date().getTime();
        window.location.href = window.location.pathname + '?t=' + timestamp;
    }
    
    // --- Cập nhật biểu đồ với dữ liệu mới (không reload trang) ---
    function updateChartsWithNewData() {
        // Fetch dữ liệu mới từ server
        fetch(window.location.pathname + '?ajax=true&t=' + new Date().getTime(), {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => response.text())
        .then(html => {
            // Parse HTML để lấy dữ liệu mới
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            
            // Tìm script tag chứa dữ liệu
            const scripts = doc.querySelectorAll('script');
            scripts.forEach(script => {
                if (script.textContent.includes('weeklyLabels')) {
                    // Extract và update data
                    // Nếu có dữ liệu mới, update charts
                    console.log("Dữ liệu mới đã được cập nhật");
                }
            });
        })
        .catch(error => {
            console.error("Lỗi khi fetch dữ liệu mới:", error);
        });
    }

    // --- Tự động refresh ---
    function startAutoRefresh() {
        // SỬA: Refresh mỗi 10 giây để cập nhật dữ liệu mới nhất ngay khi có đơn hàng mới
        refreshInterval = setInterval(() => {
            refreshData();
        }, 10000); // 10 giây - cập nhật nhanh hơn
    }

    function stopAutoRefresh() {
        if (refreshInterval) {
            clearInterval(refreshInterval);
            refreshInterval = null;
        }
    }

    // --- Cập nhật thời gian cuối ---
    function updateLastUpdateTime() {
        lastUpdateTime = new Date();
        const statusElement = document.getElementById('refreshStatus');
        if (statusElement) {
            statusElement.innerHTML = 
                '<i class="fas fa-clock me-1"></i>Cập nhật: ' + lastUpdateTime.toLocaleTimeString('vi-VN');
        }
    }

    // --- Xử lý khi tab được focus ---
    document.addEventListener('visibilitychange', function() {
        if (!document.hidden) {
            // SỬA: Refresh ngay khi quay lại tab để cập nhật dữ liệu mới nhất
            console.log("Tab được focus lại, đang refresh dữ liệu...");
            refreshData();
        }
    });
    
    // --- Lắng nghe sự kiện từ các tab khác (nếu có) ---
    window.addEventListener('storage', function(e) {
        // Nếu có đơn hàng mới được giao từ tab khác, refresh
        if (e.key === 'orderShipped' && e.newValue) {
            console.log("Phát hiện đơn hàng mới được giao từ tab khác, đang refresh...");
            refreshData();
        }
    });

    // --- Dọn dẹp khi rời trang ---
    window.addEventListener('beforeunload', function() {
        stopAutoRefresh();
        if (weeklyChart) {
            weeklyChart.destroy();
        }
        if (monthlyChart) {
            monthlyChart.destroy();
        }
    });

</script>

</body>
</html>