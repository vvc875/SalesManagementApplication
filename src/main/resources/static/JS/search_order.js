document.addEventListener("DOMContentLoaded", function () {
    // === LẤY CÁC PHẦN TỬ TRÊN TRANG ===
    const searchTypeSelect = document.getElementById("searchType");
    const keywordInputGroup = document.getElementById("keyword-input-group");
    const searchInput = document.getElementById("searchInput");
    const dateInputGroup = document.getElementById("date-input-group"); // Thêm ô ngày
    const searchDateInput = document.getElementById("searchDateInput"); // Thêm ô ngày
    const searchButton = document.getElementById("searchButton");
    const showAllButton = document.getElementById("showAllButton");
    const searchButtonsGroup = document.getElementById("search-buttons");
    const resultContainer = document.getElementById("result");

    // Form Tạo đơn hàng (nếu có trong HTML)
    const formPanel = document.getElementById("orderFormPanel");
    const formTitle = document.getElementById("formTitle");
    const addOrderBtn = document.getElementById('addOrderBtn');
    const closeFormBtn = document.getElementById("closeFormBtn");
    const saveOrderBtn = document.getElementById("saveOrderBtn");
    const orderIdInput = document.getElementById("orderId");
    const customerIdInput = document.getElementById("customerId");
    const employeeIdInput = document.getElementById("employeeId");

    // === ĐỌC CSRF TOKEN (QUAN TRỌNG) ===
    const csrfToken = document.querySelector("meta[name='_csrf']")?.getAttribute("content");
    const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.getAttribute("content");
    if (!csrfToken || !csrfHeader) {
        console.error("CSRF token not found! Update/Delete/Create operations might fail.");
        alert("Lỗi cấu hình bảo mật. Các chức năng Thêm/Sửa/Xóa có thể không hoạt động.");
    }

    // === API URL (Khớp với Controller) ===
    const API_BASE_URL = 'http://localhost:8080/orders';

    // === GẮN CÁC SỰ KIỆN ===
    fetchAllOrders(); // Tải ban đầu

    searchTypeSelect.addEventListener('change', handleSearchTypeChange);
    searchButton.addEventListener("click", performSearch);
    showAllButton.addEventListener("click", fetchAllOrders);

    // Sự kiện cho Form Tạo đơn (nếu có)
    if (addOrderBtn) addOrderBtn.addEventListener('click', handleAddClick);
    if (closeFormBtn) closeFormBtn.addEventListener("click", closeFormPanel);
    if (saveOrderBtn) saveOrderBtn.addEventListener("click", handleSaveOrder);

    // Dropdown handler (nếu có)
    const dropdownToggle = document.querySelector('.dropdown-toggle');
    const dropdown = document.querySelector('.dropdown');
    if(dropdownToggle && dropdown) {
        dropdownToggle.addEventListener('click', (event) => { event.preventDefault(); dropdown.classList.toggle('active'); });
    }
    window.addEventListener('click', (e) => {
        if(dropdown && !dropdown.contains(e.target) && dropdownToggle && !dropdownToggle.contains(e.target)) {
            dropdown.classList.remove('active');
        }
    });

    // Xử lý click trong bảng kết quả (Chi tiết, Xóa, Cập nhật trạng thái)
    resultContainer.addEventListener('click', function(event) {
        const detailButton = event.target.closest('.view-details-btn'); // Nút xem chi tiết
        const deleteButton = event.target.closest('.delete-btn');     // Nút xóa
        const editStatusButton = event.target.closest('.edit-status-btn'); // Nút cập nhật trạng thái

        if (detailButton) {
            viewOrderDetails(detailButton.dataset.id);
        } else if (deleteButton) {
            handleDeleteOrder(deleteButton.dataset.id);
        } else if (editStatusButton) { // Xử lý click nút cập nhật trạng thái
            handleUpdateStatusClick(editStatusButton.dataset.id); // Gọi hàm xử lý mới
        }
    });

    // === CÁC HÀM ĐIỀU KHIỂN FORM TẠO ĐƠN ===
    function openFormPanel() { if(formPanel) formPanel.classList.add('active'); }
    function closeFormPanel() { if(formPanel) formPanel.classList.remove('active'); }

    function handleAddClick() {
        closeFormPanel();
        setTimeout(() => {
            if(document.getElementById("orderForm")) document.getElementById("orderForm").reset();
            if(formTitle) formTitle.textContent = "Tạo đơn hàng mới";
            openFormPanel();
        }, 100);
    }

    function handleSaveOrder() {
        const customerId = customerIdInput.value.trim();
        const employeeId = employeeIdInput.value.trim();
        if (!customerId || !employeeId) {
            alert("Vui lòng điền ID Khách hàng và ID Nhân viên.");
            return;
        }
        const orderData = { customerId, employeeId };
        createOrder(orderData);
    }

    // === CÁC HÀM GỌI API ===

    // Tạo Order (POST /orders) - Gửi CSRF
    async function createOrder(orderData) {
        const headers = { 'Content-Type': 'application/json' };
        if (csrfHeader && csrfToken) headers[csrfHeader] = csrfToken;
        else { console.error("CSRF Headers not found for POST."); alert("Lỗi bảo mật."); return; }

        try {
            const response = await fetch(API_BASE_URL, { method: 'POST', headers: headers, body: JSON.stringify(orderData) });
            if (!response.ok) {
                const errorText = await response.text();
                try { const errorJson = JSON.parse(errorText); throw new Error(`Tạo thất bại: ${errorJson.message || errorText}`); }
                catch(e) { throw new Error(`Tạo thất bại (${response.status}): ${errorText}`); }
            }
            const newOrder = await response.json();
            alert(`Đã tạo đơn hàng ${newOrder.id}. Chuyển sang trang chi tiết.`);
            closeFormPanel(); fetchAllOrders(); viewOrderDetails(newOrder.id);
        } catch (error) { console.error("Lỗi:", error); alert(error.message); }
    }

    // Xóa Order (DELETE /orders/{id}) - Gửi CSRF
    async function handleDeleteOrder(orderId) {
        if (!confirm(`Xóa đơn hàng ${orderId}? Chi tiết và tồn kho sẽ cập nhật.`)) { return; }
        const headers = {};
        if (csrfHeader && csrfToken) headers[csrfHeader] = csrfToken;
        else { console.error("CSRF Headers not found for DELETE."); alert("Lỗi bảo mật."); return; }

        try {
            const response = await fetch(`${API_BASE_URL}/${orderId}`, { method: 'DELETE', headers: headers });
            if (!response.ok) {
                const errorText = await response.text();
                 try { const errorJson = JSON.parse(errorText); throw new Error(`Lỗi xóa (${response.status}): ${errorJson.message || errorText}`); }
                 catch(e) { throw new Error(`Lỗi xóa (${response.status}): ${errorText}`); }
            }
            alert(`Đã xóa đơn hàng ${orderId}`); fetchAllOrders();
        } catch (error) { console.error("Lỗi:", error); alert(error.message); }
    }

    /**
     * ==============================================
     * BỔ SUNG: Hàm gọi API cập nhật trạng thái (PUT /orders/{id}/status)
     * ==============================================
     * Gửi CSRF Token và trạng thái mới trong body.
     */
    async function updateOrderStatus(orderId, newStatus) {
        const headers = { 'Content-Type': 'application/json' }; // PUT cần Content-Type
         if (csrfHeader && csrfToken) headers[csrfHeader] = csrfToken; // Thêm CSRF
         else {
             console.error("CSRF Headers not found for PUT request.");
             alert("Lỗi cấu hình bảo mật. Không thể cập nhật trạng thái.");
             return; // Dừng lại nếu thiếu token
         }

        try {
            // Gọi endpoint PUT /orders/{orderId}/status
            const response = await fetch(`${API_BASE_URL}/${orderId}/status`, {
                method: 'PUT',
                headers: headers,
                // Gửi trạng thái mới dạng chuỗi JSON (Controller nhận String)
                body: JSON.stringify(newStatus)
            });

            if (!response.ok) {
                const errorText = await response.text();
                 try { // Thử parse lỗi JSON từ Spring Boot
                     const errorJson = JSON.parse(errorText);
                     // Ưu tiên message từ JSON lỗi, nếu không có thì dùng text
                     throw new Error(`Cập nhật trạng thái thất bại: ${errorJson.message || errorText}`);
                 } catch(e) { // Nếu không parse được JSON, dùng text lỗi
                    throw new Error(`Cập nhật trạng thái thất bại (${response.status}): ${errorText}`);
                 }
            }

            // Thông báo thành công và tải lại danh sách
            alert(`Đã cập nhật trạng thái đơn hàng ${orderId} thành ${newStatus}`);
            fetchAllOrders();

        } catch (error) {
            // Hiển thị lỗi cho người dùng
            console.error("Lỗi cập nhật trạng thái:", error);
            alert(error.message); // Hiển thị lỗi đã được xử lý ở trên
        }
    }


    // === SEARCH AND DISPLAY FUNCTIONS ===

    // Ẩn/hiện ô nhập liệu tìm kiếm theo loại
    function handleSearchTypeChange() {
        const type = this.value;
        keywordInputGroup.classList.add('hidden');
        dateInputGroup.classList.add('hidden'); // Ẩn ô ngày

        if (type === 'id') {
            keywordInputGroup.classList.remove('hidden');
            keywordInputGroup.querySelector('label').textContent = 'Nhập ID Đơn hàng:';
            searchInput.placeholder = 'ORxxx...';
        } else if (type === 'date') {
            dateInputGroup.classList.remove('hidden'); // Hiện ô ngày
        }
        // Thêm các loại tìm kiếm khác nếu cần (ví dụ: customerId, employeeId)

        // Hiện/ẩn nút tìm kiếm
        if (type) { searchButtonsGroup.classList.remove('hidden'); }
        else { searchButtonsGroup.classList.add('hidden'); }
    }

    // Thực hiện tìm kiếm (GET)
    function performSearch() {
        const searchType = searchTypeSelect.value;
        let apiUrl = '';
        let title = '';
        let searchTermDisplay = '';

        if (searchType === 'id') {
            const keyword = searchInput.value.trim();
            if (!keyword) { alert("Nhập ID đơn hàng."); return; }
            apiUrl = `${API_BASE_URL}/${encodeURIComponent(keyword)}`;
            searchTermDisplay = `ID: ${keyword}`;
            title = `Kết quả cho ${searchTermDisplay}`;
        } else if (searchType === 'date') {
            const selectedDate = searchDateInput.value;
            if (!selectedDate) { alert("Chọn ngày."); return; }
            apiUrl = `${API_BASE_URL}/date?orderDate=${selectedDate}`; // Gọi endpoint /date
            searchTermDisplay = `ngày ${selectedDate}`;
            title = `Kết quả cho ${searchTermDisplay}`;
        }
        // Thêm các loại tìm kiếm khác nếu cần
        else { alert("Loại tìm kiếm không hợp lệ."); return; }

        resultContainer.innerHTML = `<p>Đang tìm ${searchTermDisplay}...</p>`;

        fetch(apiUrl) // GET không cần CSRF
            .then(response => {
                if (!response.ok) {
                    if (response.status === 404 && searchType === 'id') { throw new Error(`Không tìm thấy ID: ${searchInput.value.trim()}`); }
                    return response.text().then(text => {throw new Error(`Lỗi ${response.status}: ${text || 'Không thể tìm'}`)});
                }
                return response.json();
            })
            .then(data => {
                const dataArray = Array.isArray(data) ? data : (data ? [data] : []);
                displayOrders(dataArray, title);
            })
            .catch(error => {
                console.error("Lỗi:", error);
                resultContainer.innerHTML = `<h3>${title}</h3><p style="color:red;">❌ ${error.message}</p>`;
            });
    }

    // Tải tất cả Order (GET /orders)
    function fetchAllOrders() {
        searchTypeSelect.value = "id"; // Reset về tìm theo ID
        searchInput.value = "";
        searchDateInput.value = ""; // Reset ô ngày
        handleSearchTypeChange.call(searchTypeSelect); // Reset giao diện tìm kiếm
        resultContainer.innerHTML = "<p>Đang tải danh sách...</p>";

        fetch(API_BASE_URL) // GET không cần CSRF
            .then(response => {
                if (!response.ok) throw new Error("Lỗi tải danh sách");
                return response.json();
            })
            .then(data => displayOrders(data, "Tất cả đơn hàng"))
            .catch(error => {
                console.error("Lỗi:", error);
                resultContainer.innerHTML = `<p style='color:red;'>❌ ${error.message}</p>`;
            });
    }

    // Hiển thị bảng Order
    function displayOrders(data, title) {
        if (!data || data.length === 0) {
            resultContainer.innerHTML = `<h3>${title}</h3><p>❌ Không có đơn hàng nào.</p>`;
            return;
        }
        let html = `<h3>${title} (${data.length})</h3>`;
        html += `<table><thead><tr><th>ID</th><th>Khách hàng</th><th>Nhân viên</th><th>Ngày đặt</th><th>Trạng thái</th><th>Tổng tiền (VND)</th><th>Hành động</th></tr></thead><tbody>`;
        data.forEach(order => {
            const customerName = order.customer?.name ?? (order.customerId || 'N/A');
            const employeeName = order.employee?.name ?? (order.employeeId || 'N/A');
            const formattedTotal = formatCurrency(order.totalAmount);
            const orderStatus = order.status || 'N/A';
            html += `
                <tr>
                    <td>${order.id}</td>
                    <td>${customerName}</td>
                    <td>${employeeName}</td>
                    <td>${order.orderDate || 'N/A'}</td>
                    <td>${orderStatus}</td>
                    <td><strong>${formattedTotal}</strong></td>
                    <td class="action-buttons">
                        <button class="view-details-btn btn" data-id="${order.id}" title="Xem chi tiết"><i class="fas fa-eye"></i></button>
                        <button class="edit-status-btn btn" data-id="${order.id}" title="Cập nhật trạng thái"><i class="fas fa-sync-alt"></i></button>
                         <button class="delete-btn btn" data-id="${order.id}" title="Xóa đơn hàng"><i class="fas fa-trash"></i></button>
                    </td>
                </tr>`;
        });
        html += `</tbody></table>`;
        resultContainer.innerHTML = html;
    }

    // Chuyển trang chi tiết
    function viewOrderDetails(orderId) {
        window.location.href = `page_order_detail.html?orderId=${orderId}`;
    }

    // Format tiền tệ
    function formatCurrency(amount) {
        const numericAmount = Number(amount) || 0;
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(numericAmount);
    }

    /**
     * ==============================================
     * BỔ SUNG: Hàm xử lý khi bấm nút cập nhật trạng thái
     * ==============================================
     * Hiện prompt để người dùng nhập trạng thái mới.
     */
    function handleUpdateStatusClick(orderId) {
        // Hiện hộp thoại yêu cầu nhập trạng thái mới
        const currentStatus = prompt(`Nhập trạng thái mới cho đơn hàng ${orderId} (PENDING, PROCESSING, COMPLETED, CANCELLED):`);
        // Lấy giá trị, loại bỏ khoảng trắng thừa, chuyển thành chữ hoa
        const newStatus = currentStatus ? currentStatus.trim().toUpperCase() : null;

        // Kiểm tra xem người dùng có nhập gì không và có hợp lệ không
        if (newStatus && ['PENDING', 'PROCESSING', 'COMPLETED', 'CANCELLED'].includes(newStatus)) {
            // Nếu hợp lệ, gọi hàm gọi API
            updateOrderStatus(orderId, newStatus);
        } else if(newStatus !== null) { // Chỉ báo lỗi nếu người dùng nhập gì đó sai (không phải bấm Cancel)
            alert("Trạng thái không hợp lệ! Chỉ chấp nhận PENDING, PROCESSING, COMPLETED, CANCELLED.");
        }
        // Nếu newStatus là null (người dùng bấm Cancel), không làm gì cả.
    }
    // Hàm updateOrderStatus(orderId, newStatus) đã được định nghĩa ở trên (phần API Calls)

}); // Kết thúc DOMContentLoaded

