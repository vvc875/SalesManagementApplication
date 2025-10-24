document.addEventListener('DOMContentLoaded', () => {

    const API_URL = 'http://localhost:8080/statistics';
    const form = document.getElementById('topCustomersForm');
    const limitInput = document.getElementById('limitInput');
    const dateInput = document.getElementById('dateInput');
    const resultsContainer = document.getElementById('resultsContainer');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        loadTopCustomers();
    });

    async function loadTopCustomers() {
        resultsContainer.innerHTML = `<p class="loading-message">Đang tải dữ liệu...</p>`;
        
        const limit = limitInput.value || 10;
        const date = dateInput.value;
        
        let url = `${API_URL}/customers/top?limit=${limit}`;
        if (date) {
            url += `&date=${date}`; // Thêm tham số date nếu có
        }
        
        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error('Lỗi khi tải dữ liệu. Vui lòng kiểm tra lại server.');
            }
            const data = await response.json(); // Data là List<TopCustomerDTO>
            renderTable(data);
        } catch (error) {
            resultsContainer.innerHTML = `<p class="error-message">${error.message}</p>`;
        }
    }

    function renderTable(data) {
        if (!data || data.length === 0) {
            resultsContainer.innerHTML = `<p class="no-data-message">Không tìm thấy khách hàng nào.</p>`;
            return;
        }

        let tableHtml = `
            <table class="results-table">
                <thead>
                    <tr>
                        <th>Hạng</th>
                        <th>ID Khách hàng</th>
                        <th>Tên Khách hàng</th>
                        <th class="col-number">Tổng chi tiêu (VND)</th>
                    </tr>
                </thead>
                <tbody>
        `;
        
        data.forEach((item, index) => {
            tableHtml += `
                <tr>
                    <td>${index + 1}</td>
                    <td>${item.customerId}</td>
                    <td>${item.customerName}</td>
                    <td class="col-number">${formatCurrency(item.totalAmountSpent)}</td>
                </tr>
            `;
        });
        
        tableHtml += `</tbody></table>`;
        resultsContainer.innerHTML = tableHtml;
    }
    
    // Hàm tiện ích
    function formatCurrency(amount) {
        if (typeof amount !== 'number') amount = 0;
        return amount.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
    }

    // --- TỰ ĐỘNG CHẠY KHI TẢI TRANG (NẾU CÓ) ---
    // Kiểm tra xem trang có được chuyển đến từ trang chủ với ngày cụ thể không
    const urlParams = new URLSearchParams(window.location.search);
    const autoDate = urlParams.get('date');

    if (autoDate) {
        dateInput.value = autoDate;
        loadTopCustomers();
    }
});
