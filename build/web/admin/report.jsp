<%--  
    File: /admin/report.jsp  
    Trang Admin hiển thị Biểu đồ Báo cáo Doanh thu  
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Admin - Báo cáo Doanh thu</title>

        <!-- Bootstrap -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        
        <!-- Thư viện Chart.js -->
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        
        <style>
            body { font-size: .875rem; }
            .sidebar { position: fixed; top: 0; bottom: 0; left: 0; z-index: 100; padding: 48px 0 0; box-shadow: inset -1px 0 0 rgba(0, 0, 0, .1); }
            main { padding-top: 1rem; }
            .admin-sidebar .nav-link { font-weight: 500; color: #333; }
            .admin-sidebar .nav-link.active { color: #004a99; }
            .admin-sidebar .nav-link:hover { color: #f58220; }
            .chart-container { max-width: 900px; margin: 2rem auto; }
        </style>
    </head>

    <body>
        <jsp:include page="header_admin.jsp"></jsp:include>

        <div class="container-fluid">
            <div class="row">
                <jsp:include page="sidebar_admin.jsp"></jsp:include>

                <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                    
                    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                        <h1 class="h2">Báo cáo Doanh thu</h1>
                        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-sm btn-outline-secondary">
                           <i class="fas fa-arrow-left me-1"></i> Quay lại Dashboard
                        </a>
                    </div>

                    <!-- Biểu đồ Doanh thu Tuần -->
                    <div class="chart-container">
                        <h4 class="text-center">Doanh thu 7 ngày qua</h4>
                        <canvas id="weeklyRevenueChart"></canvas>
                    </div>

                    <hr class="my-4">

                    <!-- Biểu đồ Doanh thu Tháng -->
                    <div class="chart-container">
                        <h4 class="text-center">Doanh thu 12 tháng qua</h4>
                        <canvas id="monthlyRevenueChart"></canvas>
                    </div>

                </main>
            </div>
        </div>
        
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        
        <!-- === SCRIPT VẼ BIỂU ĐỒ === -->
        <script>
            // Lấy dữ liệu JSON từ servlet (Gson đã sinh ra JSON)
            var weeklyLabels = ${weeklyLabels};
            var weeklyData = ${weeklyData};
            var monthlyLabels = ${monthlyLabels};
            var monthlyData = ${monthlyData};

            // Debug ra console để kiểm tra dữ liệu
            console.log("Tuần:", weeklyLabels, weeklyData);
            console.log("Tháng:", monthlyLabels, monthlyData);

            // Kiểm tra có dữ liệu không
            if (weeklyLabels && weeklyData) {
                // --- Biểu đồ Tuần ---
                var ctxWeek = document.getElementById('weeklyRevenueChart').getContext('2d');
                var weeklyChart = new Chart(ctxWeek, {
                    type: 'line',
                    data: {
                        labels: weeklyLabels,
                        datasets: [{
                            label: 'Doanh thu (đ)',
                            data: weeklyData,
                            backgroundColor: 'rgba(245, 130, 32, 0.2)',
                            borderColor: 'rgba(245, 130, 32, 1)',
                            borderWidth: 2,
                            fill: true
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    callback: function(value) {
                                        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
                                    }
                                }
                            }
                        }
                    }
                });
            }

            if (monthlyLabels && monthlyData) {
                // --- Biểu đồ Tháng ---
                var ctxMonth = document.getElementById('monthlyRevenueChart').getContext('2d');
                var monthlyChart = new Chart(ctxMonth, {
                    type: 'bar',
                    data: {
                        labels: monthlyLabels,
                        datasets: [{
                            label: 'Doanh thu (đ)',
                            data: monthlyData,
                            backgroundColor: 'rgba(0, 74, 153, 0.7)',
                            borderColor: 'rgba(0, 74, 153, 1)',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    callback: function(value) {
                                        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        </script>
    </body>
</html>
