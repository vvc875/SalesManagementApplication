document.addEventListener('DOMContentLoaded', function() {

    loadDashboardData();
});

function loadDashboardData() {

    const apiUrl = '/api/stats';


    fetch(apiUrl)
        .then(response => {

            if (!response.ok) {

                throw new Error(`Lỗi mạng: ${response.statusText}`);
            }
            return response.json();
        })
        .then(data => {
            const formattedRevenue = data.revenue.toLocaleString('vi-VN');

            updateElementText('total-products', data.totalProducts);
            updateElementText('total-customers', data.totalCustomers);
            updateElementText('today-invoices', data.todayInvoices);
            updateElementText('today-revenue', formattedRevenue);
        })
        .catch(error => {

            console.error('Không thể tải dữ liệu dashboard:', error);


            const errorMessage = "Lỗi";
            updateElementText('total-products', errorMessage);
            updateElementText('total-customers', errorMessage);
            updateElementText('today-invoices', errorMessage);
            updateElementText('today-revenue', errorMessage);
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