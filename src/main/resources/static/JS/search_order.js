document.addEventListener('DOMContentLoaded', function () {
    const searchTypeSelect = document.getElementById("searchType");
    const keywordInputGroup = document.getElementById("keyword-input-group");
    const searchInput = document.getElementById("searchInput");
    const searchButton = document.getElementById("searchButton");
    const showAllButton = document.getElementById("showAllButton");
    const searchButtonsGroup = document.getElementById("search-buttons");
    const resultContainer = document.getElementById("result");

    const formPanel = document.getElementById("orderFormPanel");
    const formTitle = document.getElementById("formTitle");
    const addOrderBtn = document.getElementById('addOrderBtn');
    const closeFormBtn = document.getElementById("closeFormBtn");
    const saveOrderBtn = document.getElementById("saveOrderBtn"); 

    const orderIdInput = document.getElementById("orderId");
    const customerIdInput = document.getElementById("customerId");
    const employeeIdInput = document.getElementById("employeeId");
    
    const API_BASE_URL = 'http://localhost:8080/orders';    
    fetchAllOrders();

    searchTypeSelect.addEventListener('change', handleSearchTypeChange);
    searchButton.addEventListener("click", performSearch); 
    showAllButton.addEventListener("click", fetchAllOrders); 
    addOrderBtn.addEventListener('click', handleAddClick);
    closeFormBtn.addEventListener("click", closeFormPanel);
    saveOrderBtn.addEventListener("click", handleSaveOrder); 

    const dropdownToggle = document.querySelector('.dropdown-toggle');
    const dropdown = document.querySelector('.dropdown');

    if(dropdownToggle && dropdown) {
        dropdownToggle.addEventListener('click', function(event){
            event.preventDefault();
            dropdown.classList.toggle('active');
        })
    }

    window.addEventListener('click', function(e){
        if(dropdown && !dropdown.contains(e.target) && !dropdownToggle.contains(e.target)) {
            dropdown.classList.remove('active');
    }
    });
    
    resultContainer.addEventListener('click', function(event) {
        const target = event.target; 
        const detailButton = target.closest('.btn-detail');
        if (detailButton) {

            viewOrderDetails(detailButton.dataset.id); 
            return; 
        }
        const deleteButton = target.closest('.delete-btn');
        if (deleteButton) {
            handleDeleteOrder(deleteButton.dataset.id); 
            return; 
        }
    });

    function openFormPanel() {
        formPanel.classList.add('active'); 
    }
    function closeFormPanel() {
        formPanel.classList.remove('active'); 
    }

    function handleAddClick() {
        closeFormPanel();
        setTimeout(() => {
            document.getElementById("orderForm").reset();
            formTitle.textContent = "Thêm đơn hàng";
            orderIdInput.value = "";
            openFormPanel();
        }, 100);
    }

    function handleSaveOrder() {
        const customerId = customerIdInput.value.trim();
        const employeeId = employeeIdInput.value.trim();
        if (!customerId || !employeeId) {
            alert("Vui lòng điền đầy đủ ID Khách hàng và ID Nhân viên.");
            return;
        }
        const orderData = { customerId, employeeId };
        createOrder(orderData);
    }

    async function createOrder(orderData) {
        try {
            const response = await fetch(API_BASE_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(orderData)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Tạo đơn hàng thất bại: ${errorText}`);
            }

            const newOrder = await response.json();
            const newOrderId = newOrder.id; 

            alert(`Đã tạo đơn hàng rỗng thành công với ID: ${newOrderId}. Chuyển sang trang chi tiết để thêm sản phẩm.`);
            closeFormPanel();
            fetchAllOrders(); 
            viewOrderDetails(newOrderId); 

        } catch (error) {
            console.error("Lỗi tạo đơn hàng:", error);
            alert(error.message);
        }
    }
    
    function performSearch() {
        const searchType = searchTypeSelect.value;
        const keyword = searchInput.value.trim();

        if (!keyword) {
            alert("Vui lòng nhập từ khóa tìm kiếm.");
            return;
        }

        let apiUrl = '';
        if (searchType === 'id') {
            apiUrl = `${API_BASE_URL}/${keyword}`;
            title = `Kết quả cho ID: ${keyword}`;
            
            fetch(apiUrl)
                .then(response => {
                    if (!response.ok) throw new Error(`Không tìm thấy đơn hàng ID: ${keyword}`);
                    return response.json();
                })
                .then(order => displayOrders([order], title)) 
                .catch(error => { 
                    console.error("Lỗi tìm kiếm ID:", error);
                    resultContainer.innerHTML = `<tr><td colspan="7" style="text-align: center; color: red;">${error.message}</td></tr>`;
                });
            return;
        } 
        else if (searchType === 'customer' || searchType === 'status') {
            apiUrl = `${API_BASE_URL}/search?type=${searchType}&keyword=${encodeURIComponent(keyword)}`;
            title = `Kết quả tìm kiếm theo ${searchType}`;
        }

        if (!apiUrl) return;

        fetch(apiUrl)
            .then(response => {
                if (!response.ok) throw new Error(`Lỗi Status: ${response.status} khi tìm kiếm.`);
                return response.json();
            })
            .then(data => displayOrders(data, title))
            .catch(error => { 
                console.error("Lỗi tìm kiếm:", error); 
                resultContainer.innerHTML = `<tr><td colspan="7" style="text-align: center; color: red;">❌ Không tìm thấy đơn hàng nào khớp.</td></tr>`; 
            });
    }
    
    function fetchAllOrders() {
        searchTypeSelect.value = "";
        handleSearchTypeChange.call(searchTypeSelect); 
        resultContainer.innerHTML = "<p>Đang tải danh sách đơn hàng...</p>";
        const urlParams = new URLSearchParams(window.location.search);
        const dateParam = urlParams.get('date');

        let apiUrl = API_BASE_URL;
        let title = "Tất cả đơn hàng";

        if (dateParam) {
            // Giả định endpoint lọc theo ngày là '/by-date?date=YYYY-MM-DD'
            // Bạn cần tạo endpoint này trong OrderController để gọi hàm findOrdersByDate
            apiUrl = `${API_BASE_URL}/by-date?date=${dateParam}`;
            title = `Danh sách đơn hàng ngày: ${dateParam}`;
        }

        fetch(apiUrl)
            .then(response => {
                if (!response.ok) {
                    if(dateParam) throw new Error(`Lỗi tải danh sách cho ngày ${dateParam}`);
                    throw new Error("Lỗi khi gọi API");
                }
                return response.json();
            })
            .then(data => displayOrders(data, title))
            .catch(error => { 
                console.error("Lỗi:", error); 
                resultContainer.innerHTML = `<p style='color:red;' colspan='7'>❌ ${error.message}</p>`; 
            });
    }

    function displayOrders(data, title) {
        if (!data || data.length === 0) {
            resultContainer.innerHTML = `<h3>${title}</h3><p>❌ Không có đơn hàng nào trong danh sách.</p>`;
            return;
        }

        let html = `<h3>${title} (${data.length} kết quả)</h3>`;
        html += `
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Ngày đặt đơn</th>
                        <th>Tổng giá trị đơn (VND)</th>
                        <th>Trạng thái đơn</th>
                        <th>Khách hàng</th>
                        <th>Nhân viên phụ trách đơn</th> 
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
        `;
        data.forEach(order => {
            const customerName = order.customer ? order.customer.name : (order.customerId || 'N/A');
            const employeeName = order.employee ? order.employee.name : (order.employeeId || 'N/A');
            const formattedTotal = (order.totalAmount || 0).toLocaleString('vi-VN');
            html += `
                <tr>
                    <td>${order.id}</td>
                    
                    <td>${order.orderDate || 'N/A'}</td>
                    
                    <td>${formattedTotal}</td>
                    
                    <td>${order.status || 'N/A'}</td>
                    
                    <td>${customerName}</td>
                    
                    <td>${employeeName}</td>
                    
                    <td class="action-buttons">
                        <button class="btn btn-detail" data-id="${order.id}" title="Xem chi tiết đơn hàng">
                            <i class="fas fa-eye" data-id="${order.id}"></i>
                        </button>

                        <button class="delete-btn" data-id="${order.id}" title="Xóa đơn hàng">
                            <i class="fas fa-trash" data-id="${order.id}"></i>
                        </button>
                    </td>
                </tr>
            `;
        });
        html += `</tbody></table>`;
        resultContainer.innerHTML = html;
    }

    function viewOrderDetails(orderId) {
        window.location.href = `page_order_detail?id=${orderId}`;
    }

    async function handleDeleteOrder(orderId) {
        if (!confirm(`Bạn có chắc chắn muốn xóa đơn hàng ${orderId} không?`)) {
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/${orderId}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Lỗi khi xóa: ${errorText}`);
            }

            alert(`Đã xóa thành công đơn hàng ${orderId}`);
            fetchAllOrders(); 

        } catch (error) {
            console.error("Lỗi xóa đơn hàng:", error);
            alert(error.message);
        }
    }

    function handleSearchTypeChange() {
        const type = this.value;
        if (type) {
            keywordInputGroup.classList.remove('hidden');
            searchButtonsGroup.classList.remove('hidden');
            // Cập nhật nhãn và placeholder nếu cần
            document.querySelector('#keyword-input-group label').textContent = (type === 'id') ? 'Nhập ID đơn hàng:' : 'Nhập tên/trạng thái:';
            searchInput.placeholder = (type === 'id') ? 'ORxxx' : 'Nhập từ khóa...';
        } 
        else {
            keywordInputGroup.classList.add('hidden');
            searchButtonsGroup.classList.add('hidden');
        }
    }
    
    // Hàm formatCurrency giữ nguyên
    function formatCurrency(amount) {
        if (typeof amount !== 'number') return '0 VND';
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
    }
});