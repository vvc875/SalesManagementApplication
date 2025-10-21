document.addEventListener("DOMContentLoaded", function () {
    // === LẤY CÁC PHẦN TỬ TRÊN TRANG ===
    const searchTypeSelect = document.getElementById("searchType");
    const keywordInputGroup = document.getElementById("keyword-input-group");
    const searchInput = document.getElementById("searchInput");
    const searchButton = document.getElementById("searchButton");
    const showAllButton = document.getElementById("showAllButton");
    const resultContainer = document.getElementById("result");

    const formPanel = document.getElementById("customerFormPanel");
    const formTitle = document.getElementById("formTitle");
    const addCustomerBtn = document.getElementById("addCustomerBtn");
    const saveCustomerBtn = document.getElementById("saveCustomerBtn");
    const closeFormBtn = document.getElementById("closeFormBtn");
    
    const customerIdInput = document.getElementById("customerId");
    const customerNameInput = document.getElementById("customerName");
    const customerEmailInput = document.getElementById("customerEmail");
    const customerPhoneInput = document.getElementById("customerPhone");
    const customerAddressInput = document.getElementById("customerAddress");

    // === GẮN CÁC SỰ KIỆN ===
    fetchAllCustomers();

    searchTypeSelect.addEventListener('change', handleSearchTypeChange);
    searchButton.addEventListener("click", performSearch);
    showAllButton.addEventListener("click", fetchAllCustomers);
    addCustomerBtn.addEventListener("click", handleAddClick);
    saveCustomerBtn.addEventListener("click", handleSaveCustomer);
    closeFormBtn.addEventListener("click", closeFormPanel); // Gắn sự kiện cho nút đóng

    resultContainer.addEventListener('click', function(event) {
        const target = event.target;
        if (target.classList.contains('edit-btn')) {
            handleEditClick(target.dataset.id);
        }
        if (target.classList.contains('delete-btn')) {
            handleDeleteClick(target.dataset.id);
        }
    });
    
    // === CÁC HÀM ĐIỀU KHIỂN FORM PANEL ===
    function openFormPanel() {
        formPanel.classList.add('active'); // Thêm class 'active' để trượt xuống
    }
    function closeFormPanel() {
        formPanel.classList.remove('active'); // Xóa class 'active' để trượt lên
    }

    // === CÁC HÀM XỬ LÝ SỰ KIỆN ===
    function handleAddClick() {
        closeFormPanel(); // Đóng form cũ nếu đang mở
        setTimeout(() => { // Thêm độ trễ nhỏ để hiệu ứng mượt hơn
            document.getElementById("customerForm").reset();
            formTitle.textContent = "Thêm Khách Hàng Mới";
            customerIdInput.value = "";
            openFormPanel();
        }, 100);
    }

    function handleEditClick(id) {
        closeFormPanel(); // Đóng form cũ nếu đang mở
        setTimeout(() => {
            fetch(`http://localhost:8080/customer/${id}`)
                .then(response => response.json())
                .then(customer => {
                    formTitle.textContent = "Sửa Thông Tin Khách Hàng";
                    customerIdInput.value = customer.id;
                    customerNameInput.value = customer.name;
                    customerEmailInput.value = customer.email;
                    customerPhoneInput.value = customer.phone;
                    customerAddressInput.value = customer.address;
                    openFormPanel();
                })
                .catch(error => console.error("Lỗi:", error));
        }, 100);
    }

    function handleSaveCustomer() {
        const id = customerIdInput.value;
        const customerData = {
            name: customerNameInput.value,
            email: customerEmailInput.value,
            phone: customerPhoneInput.value,
            address: customerAddressInput.value,
        };
        if (id) {
            updateCustomer(id, customerData);
        } else {
            addCustomer(customerData);
        }
    }

    function handleDeleteClick(id) {
        if (confirm(`Bạn có chắc muốn xóa khách hàng ID: ${id}?`)) {
            deleteCustomer(id);
        }
    }

    // === CÁC HÀM GỌI API (CRUD) ===
    function addCustomer(customerData) {
        fetch('http://localhost:8080/customer', {
            method: 'POST', headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(customerData)
        })
        .then(response => {
            if (!response.ok) throw new Error('Thêm mới thất bại!');
            closeFormPanel(); 
            fetchAllCustomers();
        })
        .catch(error => console.error("Lỗi:", error));
    }

    function updateCustomer(id, customerData) {
        fetch(`http://localhost:8080/customer/${id}`, {
            method: 'PUT', headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(customerData)
        })
        .then(response => {
            if (!response.ok) throw new Error('Cập nhật thất bại!');
            closeFormPanel(); fetchAllCustomers();
        })
        .catch(error => console.error("Lỗi:", error));
    }

    function deleteCustomer(id) {
        fetch(`http://localhost:8080/customer/${id}`, { method: 'DELETE' })
        .then(response => {
            if (!response.ok) throw new Error('Xóa thất bại!');
            fetchAllCustomers();
        })
        .catch(error => console.error("Lỗi:", error));
    }

    // === CÁC HÀM TÌM KIẾM VÀ HIỂN THỊ ===
function handleSearchTypeChange() {
    const type = this.value; 

    const keywordGroup = document.getElementById('keyword-input-group');
    const searchButtonsGroup = document.getElementById('search-buttons');

    if (type) {
        keywordGroup.classList.remove('hidden');
        searchButtonsGroup.classList.remove('hidden');
    } else {
        keywordGroup.classList.add('hidden');
        searchButtonsGroup.classList.add('hidden');
    }
}

    function performSearch() {
        const searchType = searchTypeSelect.value;
        const keyword = searchInput.value.trim();

        if (!keyword) {
            resultContainer.innerHTML = "<p>⚠️ Vui lòng nhập từ khóa tìm kiếm!</p>";
            return;
        }

        let apiUrl = '';
        if (searchType === 'id') {
            apiUrl = `http://localhost:8080/customer/${encodeURIComponent(keyword)}`;
        } else { 
            apiUrl = `http://localhost:8080/customer/search?keyword=${encodeURIComponent(keyword)}`;
        }

        fetch(apiUrl)
            .then(response => {
                if (!response.ok) throw new Error(`Lỗi Status: ${response.status}`);
                return response.json();
            })
            .then(data => {
                const dataArray = Array.isArray(data) ? data : (data ? [data] : []);
                displayCustomers(dataArray, `Kết quả cho "${keyword}"`);
            })
            .catch(error => {
                console.error("Lỗi:", error);
                resultContainer.innerHTML = `<h3>Kết quả cho "${keyword}"</h3><p>❌ Không tìm thấy khách hàng nào khớp.</p>`;
            });
    }

    function fetchAllCustomers() {
        searchTypeSelect.value = ""; 
        handleSearchTypeChange.call(searchTypeSelect);
        resultContainer.innerHTML = "<p>Đang tải danh sách khách hàng...</p>";
        
        fetch('http://localhost:8080/customer')
            .then(response => {
                if (!response.ok) throw new Error("Lỗi khi gọi API");
                return response.json();
            })
            .then(data => displayCustomers(data, "Tất cả khách hàng"))
            .catch(error => {
                console.error("Lỗi:", error);
                resultContainer.innerHTML = "<p style='color:red;'>❌ Không thể tải danh sách khách hàng!</p>";
            });
    }

    function displayCustomers(data, title) {
        if (!data || data.length === 0) {
            resultContainer.innerHTML = `<h3>${title}</h3><p>❌ Không có khách hàng nào trong danh sách.</p>`;
            return;
        }

        let html = `<h3>${title} (${data.length} kết quả)</h3>`;
        html += `
            <table>
                <thead>
                    <tr>
                        <th>ID</th><th>Tên Khách Hàng</th><th>Email</th>
                        <th>Số điện thoại</th><th>Địa chỉ</th><th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
        `;

        data.forEach(customer => {
            html += `
                <tr>
                    <td>${customer.id}</td>
                    <td><strong>${customer.name}</strong></td>
                    <td>${customer.email || 'N/A'}</td>
                    <td>${customer.phone || 'N/A'}</td>
                    <td>${customer.address || 'N/A'}</td>
                    <td class="action-buttons">
                        <button class="edit-btn" data-id="${customer.id}">Sửa</button>
                        <button class="delete-btn" data-id="${customer.id}">Xóa</button>
                    </td>
                </tr>
            `;
        });
        html += `</tbody></table>`;
        resultContainer.innerHTML = html;
    }
});
