document.addEventListener('DOMContentLoaded', () => {

    const API_URL = 'http://localhost:8080/statistics';

    // Form 1: Doanh thu theo ngày
    const dailyForm = document.getElementById('dailyRevenueForm');
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    const dailyResultsContainer = document.getElementById('dailyResultsContainer');

    // Form 2: Doanh thu theo tháng
    const monthlyForm = document.getElementById('monthlyRevenueForm');
    const yearInput = document.getElementById('yearInput');
    const monthlyResultsContainer = document.getElementById('monthlyResultsContainer');

    // --- XỬ LÝ DOANH THU THEO NGÀY ---
    
    dailyForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const startDate = startDateInput.value;
        const endDate = endDateInput.value;

        if (!startDate || !endDate) {
            alert('Vui lòng chọn cả ngày bắt đầu và ngày kết thúc.');
            return;
        }
        
        loadDailyRevenue(startDate, endDate);
    });

    async function loadDailyRevenue(startDate, endDate) {
        dailyResultsContainer.innerHTML = `<p class="loading-message">Đang tải dữ liệu...</p>`;
        
        try {
            const response = await fetch(`${API_URL}/revenue/daily?startDate=${startDate}&endDate=${endDate}`);
            if (!response.ok) {
                throw new Error('Lỗi khi tải dữ liệu. Vui lòng kiểm tra lại server.');
            }
            const data = await response.json();
            renderDailyTable(data);
        } catch (error) {
            dailyResultsContainer.innerHTML = `<p class="error-message">${error.message}</p>`;
        }
    }

    function renderDailyTable(data) {
        if (!data || data.length === 0) {
            dailyResultsContainer.innerHTML = `<p class="no-data-message">Không tìm thấy doanh thu trong khoảng ngày này.</p>`;
            return;
        }

        let total = 0;
        let tableHtml = `
            <table class="results-table">
                <thead>
                    <tr>
                        <th>Ngày</th>
                        <th class="col-number">Doanh thu (VND)</th>
                    </tr>
                </thead>
                <tbody>
        `;
        
        data.forEach(item => {
            total += item.totalRevenue;
            tableHtml += `
                <tr>
                    <td>${item.date}</td>
                    <td class="col-number">${formatCurrency(item.totalRevenue)}</td>
                </tr>
            `;
        });

        // Thêm hàng tổng cộng
        tableHtml += `
                <tr style="font-weight: bold; background-color: #f4f7f6;">
                    <td>TỔNG CỘNG</td>
                    <td class="col-number">${formatCurrency(total)}</td>
                </tr>
            </tbody>
        </table>`;
        
        dailyResultsContainer.innerHTML = tableHtml;
    }

    // --- XỬ LÝ DOANH THU THEO THÁNG ---
    
    monthlyForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const year = yearInput.value;
        if (!year) {
            alert('Vui lòng nhập năm.');
            return;
        }
        loadMonthlyRevenue(year);
    });

    async function loadMonthlyRevenue(year) {
        monthlyResultsContainer.innerHTML = `<p class="loading-message">Đang tải dữ liệu...</p>`;
        
        try {
            const response = await fetch(`${API_URL}/revenue/monthly?year=${year}`);
            if (!response.ok) {
                throw new Error('Lỗi khi tải dữ liệu. Vui lòng kiểm tra lại server.');
            }
            const data = await response.json(); // Data là List<Object[]>
            renderMonthlyTable(data);
        } catch (error) {
            monthlyResultsContainer.innerHTML = `<p class="error-message">${error.message}</p>`;
        }
    }

    function renderMonthlyTable(data) {
        if (!data || data.length === 0) {
            monthlyResultsContainer.innerHTML = `<p class="no-data-message">Không tìm thấy doanh thu trong năm ${yearInput.value}.</p>`;
            return;
        }

        let total = 0;
        let tableHtml = `
            <table class="results-table">
                <thead>
                    <tr>
                        <th>Tháng</th>
                        <th class="col-number">Doanh thu (VND)</th>
                    </tr>
                </thead>
                <tbody>
        `;
        
        data.forEach(item => {
            const month = item[0];
            const revenue = item[1];
            total += revenue;
            tableHtml += `
                <tr>
                    <td>Tháng ${month}</td>
                    <td class="col-number">${formatCurrency(revenue)}</td>
                </tr>
            `;
        });
        
        // Thêm hàng tổng cộng
        tableHtml += `
                <tr style="font-weight: bold; background-color: #f4f7f6;">
                    <td>TỔNG CỘNG</td>
                    <td class="col-number">${formatCurrency(total)}</td>
                </tr>
            </tbody>
        </table>`;
        
        monthlyResultsContainer.innerHTML = tableHtml;
    }

    // --- TỰ ĐỘNG CHẠY KHI TẢI TRANG (NẾU CÓ) ---
    // Kiểm tra xem trang có được chuyển đến từ trang chủ với ngày cụ thể không
    const urlParams = new URLSearchParams(window.location.search);
    const autoStartDate = urlParams.get('startDate');
    const autoEndDate = urlParams.get('endDate');

    if (autoStartDate && autoEndDate) {
        startDateInput.value = autoStartDate;
        endDateInput.value = autoEndDate;
        loadDailyRevenue(autoStartDate, autoEndDate);
    }
    
    // Hàm tiện ích
    function formatCurrency(amount) {
        if (typeof amount !== 'number') amount = 0;
        return amount.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
    }
});
