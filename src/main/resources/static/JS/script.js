document.addEventListener("DOMContentLoaded", function () {
    loadDashboardData();

    const dropdownToggle = document.querySelector(".dropdown-toggle");
    const dropdown = document.querySelector(".dropdown");

    if (dropdownToggle && dropdown) {
        dropdownToggle.addEventListener("click", function (event) {
            event.preventDefault();
            dropdown.classList.toggle("active");
        });
    }

    window.addEventListener("click", function (e) {
        if (
            dropdown &&
            !dropdown.contains(e.target) &&
            !dropdownToggle.contains(e.target)
        ) {
            dropdown.classList.remove("active");
        }
    });

    const today = new Date();
    const year = today.getFullYear();
    const month = (today.getMonth() + 1).toString().padStart(2, "0");
    const day = today.getDate().toString().padStart(2, "0");
    const todayDateString = `${year}-${month}-${day}`;

    const productBtn = document.querySelector(".product-btn");
    const customerBtn = document.querySelector(".customer-btn");
    const orderBtn = document.querySelector(".order-btn");
    const reportBtn = document.querySelector(".report-btn");

    if (productBtn) {
        productBtn.addEventListener("click", function (e) {
            e.preventDefault();
            window.location.href = `/page_statistics_BestSellingProduct?date=${todayDateString}`;
        });
    }

    if (customerBtn) {
        customerBtn.addEventListener("click", function () {
            e.preventDefault();
            window.location.href = `/page_statistics_TopCustomers?date=${todayDateString}`;
        });
    }

    if (orderBtn) {
        orderBtn.addEventListener("click", function () {
            e.preventDefault();
            window.location.href = `/page_order?date=${todayDateString}`;
        });
    }

    if (reportBtn) {
        reportBtn.addEventListener("click", function () {
            e.preventDefault();
            window.location.href = `/page_statistics_revenue`;
        });
    }
});

function loadDashboardData() {
    const apiUrl = "/statistics/dashboard";

    fetch(apiUrl)
        .then((response) => {
            if (!response.ok) {
                if (response.status === 404) {
                    console.warn(`API ${apiUrl} chưa được tạo ở Backend.`);
                    return null;
                }
                throw new Error(`Lỗi mạng: ${response.statusText}`);
            }
            return response.json();
        })
        .then((data) => {
            if (!data) {
                const errorMessage = "N/A";
                updateElementText("total-products", errorMessage);
                updateElementText("total-customers", errorMessage);
                updateElementText("today-invoices", errorMessage);
                updateElementText("today-revenue", errorMessage);
                return;
            }
            const formattedRevenue = (data.revenue || 0).toLocaleString(
                "vi-VN"
            );

            updateElementText(
                "total-products",
                data.totalProductsSoldToday || 0
            );
            updateElementText("total-customers", data.totalCustomersToday || 0);
            updateElementText("today-invoices", data.todayInvoices || 0);
            updateElementText("today-revenue", formattedRevenue);
        })
        .catch((error) => {
            console.error("Không thể tải dữ liệu dashboard:", error);
            const errorMessage = "Lỗi";
            updateElementText("total-products", errorMessage);
            updateElementText("total-customers", errorMessage);
            updateElementText("total-invoices", errorMessage);
            updateElementText("today-revenue", errorMessage);
        });
}

function updateElementText(id, text) {
    const element = document.getElementById(id);
    if (element) {
        element.textContent = text;
    } else {
        console.warn(`Không tìm thấy phần tử với ID: ${id}`);
    }
}
