document.addEventListener("DOMContentLoaded", () => {
    const API_URL = "http://localhost:8080/statistics";
    const form = document.getElementById("bestsellingForm");
    const limitInput = document.getElementById("limitInput");
    const dateInput = document.getElementById("dateInput");
    const resultsContainer = document.getElementById("resultsContainer");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        loadBestSellingProducts();
    });

    async function loadBestSellingProducts() {
        resultsContainer.innerHTML = `<p class="loading-message">Đang tải dữ liệu...</p>`;

        const limit = limitInput.value || 10;
        const date = dateInput.value;

        let url = `${API_URL}/products/best-selling?limit=${limit}`;
        if (date) {
            url += `&date=${date}`; // Thêm tham số date nếu có
        }

        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(
                    "Lỗi khi tải dữ liệu. Vui lòng kiểm tra lại server."
                );
            }
            const data = await response.json(); // Data là List<BestSellingProductDTO>
            renderTable(data);
        } catch (error) {
            resultsContainer.innerHTML = `<p class="error-message">${error.message}</p>`;
        }
    }

    function renderTable(data) {
        if (!data || data.length === 0) {
            resultsContainer.innerHTML = `<p class="no-data-message">Không tìm thấy sản phẩm bán chạy nào.</p>`;
            return;
        }

        let tableHtml = `
            <table class="results-table">
                <thead>
                    <tr>
                        <th>Hạng</th>
                        <th>ID Sản phẩm</th>
                        <th>Tên Sản phẩm</th>
                        <th class="col-number">Tổng số lượng bán</th>
                    </tr>
                </thead>
                <tbody>
        `;

        data.forEach((item, index) => {
            tableHtml += `
                <tr>
                    <td>${index + 1}</td>
                    <td>${item.productId}</td>
                    <td>${item.productName}</td>
                    <td class="col-number">${item.totalQuantitySold}</td>
                </tr>
            `;
        });

        tableHtml += `</tbody></table>`;
        resultsContainer.innerHTML = tableHtml;
    }

    // --- TỰ ĐỘNG CHẠY KHI TẢI TRANG (NẾU CÓ) ---
    // Kiểm tra xem trang có được chuyển đến từ trang chủ với ngày cụ thể không
    const urlParams = new URLSearchParams(window.location.search);
    const autoDate = urlParams.get("date");

    if (autoDate) {
        dateInput.value = autoDate;
        loadBestSellingProducts();
    }
});
