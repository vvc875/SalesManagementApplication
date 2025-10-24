document.addEventListener('DOMContentLoaded', () => {

    const API_URL = 'http://localhost:8080/statistics';
    const loadReportBtn = document.getElementById('loadReportBtn');
    const resultsContainer = document.getElementById('resultsContainer');

    loadReportBtn.addEventListener('click', loadEmployeeRevenue);

    async function loadEmployeeRevenue() {
        resultsContainer.innerHTML = `<p class="loading-message">Đang tải dữ liệu...</p>`;
        
        try {
            const response = await fetch(`${API_URL}/revenue/by-employee`);
            if (!response.ok) {
                throw new Error('Lỗi khi tải dữ liệu. Vui lòng kiểm tra lại server.');
            }
            const data = await response.json(); // Data là List<Object[]>
            renderEmployeeTable(data);
        } catch (error) {
            resultsContainer.innerHTML = `<p class="error-message">${error.message}</p>`;
        }
    }

    function renderEmployeeTable(data) {
        if (!data || data.length === 0) {
            resultsContainer.innerHTML = `<p class="no-data-message">Không tìm thấy dữ liệu doanh thu của nhân viên.</p>`;
            return;
        }

        let tableHtml = `
            <table class="results-table">
                <thead>
                    <tr>
                        <th>ID Nhân viên</th>
                        <th>Tên Nhân viên</th>
                        <th class="col-number">Tổng doanh thu (VND)</th>
                    </tr>
                </thead>
                <tbody>
        `;
        
        data.forEach(item => {
            const employeeId = item[0];
            const employeeName = item[1];
            const revenue = item[2];
            tableHtml += `
                <tr>
                    <td>${employeeId}</td>
                    <td>${employeeName}</td>
                    <td class="col-number">${formatCurrency(revenue)}</td>
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
});
