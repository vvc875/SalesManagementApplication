document.addEventListener("DOMContentLoaded", function () {
    // === LẤY CÁC PHẦN TỬ TRÊN TRANG ===
    const searchTypeSelect = document.getElementById("searchType");
    const keywordInputGroup = document.getElementById("keyword-input-group");
    const searchInput = document.getElementById("searchInput");
    const searchButton = document.getElementById("searchButton");
    const showAllButton = document.getElementById("showAllButton");
    const resultContainer = document.getElementById("result");

    const formPanel = document.getElementById("employeeFormPanel");
    const formTitle = document.getElementById("formTitle");
    const addEmployeeBtn = document.getElementById("addEmployeeBtn");
    const saveEmployeeBtn = document.getElementById("saveEmployeeBtn");
    const closeFormBtn = document.getElementById("closeFormBtn");

    const employeeIdInput = document.getElementById("employeeId");
    const employeeNameInput = document.getElementById("employeeName");
    const employeePositionInput = document.getElementById("employeePosition");
    const employeeSalaryInput = document.getElementById("employeeSalary");
    const employeePhoneInput = document.getElementById("employeePhone");
    const employeeEmailInput = document.getElementById("employeeEmail");
    const employeeHireDateInput = document.getElementById("employeeHireDate");
    const employeeAddressInput = document.getElementById("employeeAddress");

    /**
     * ===============================================
     * SỬA 1: ĐỌC CSRF TOKEN TỪ THẺ META
     * ===============================================
     */
    const csrfToken = document.querySelector("meta[name='_csrf']")?.getAttribute("content");
    const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.getAttribute("content");

    // Kiểm tra xem token có tồn tại không
    if (!csrfToken || !csrfHeader) {
        console.error("CSRF token not found in meta tags! Make sure Spring Security is configured correctly and meta tags are present.");
        alert("Lỗi cấu hình bảo mật. Vui lòng liên hệ quản trị viên."); // Thông báo lỗi nghiêm trọng
    }

    // === GẮN CÁC SỰ KIỆN ===
    fetchAllEmployees(); // Tải danh sách ban đầu

    searchTypeSelect.addEventListener("change", handleSearchTypeChange);
    searchButton.addEventListener("click", performSearch);
    showAllButton.addEventListener("click", fetchAllEmployees);
    addEmployeeBtn.addEventListener("click", handleAddClick);
    saveEmployeeBtn.addEventListener("click", handleSaveEmployee);
    closeFormBtn.addEventListener("click", closeFormPanel);

    // Xử lý dropdown
    const dropdownToggle = document.querySelector(".dropdown-toggle");
    const dropdown = document.querySelector(".dropdown");
    if (dropdownToggle && dropdown) {
        dropdownToggle.addEventListener("click", function (event) {
            event.preventDefault();
            dropdown.classList.toggle("active");
        });
    }
    window.addEventListener("click", function (e) {
        if ( dropdown && !dropdown.contains(e.target) && dropdownToggle && !dropdownToggle.contains(e.target) ) {
            dropdown.classList.remove("active");
        }
    });

    // Xử lý click nút Sửa/Xóa trong bảng
    resultContainer.addEventListener("click", function (event) {
         // Sử dụng closest để đảm bảo bắt đúng nút, ngay cả khi click vào icon bên trong
        const editButton = event.target.closest('.edit-btn');
        const deleteButton = event.target.closest('.delete-btn');

        if (editButton) {
            handleEditClick(editButton.dataset.id);
        } else if (deleteButton) {
            handleDeleteClick(deleteButton.dataset.id);
        }
    });

    // === CÁC HÀM ĐIỀU KHIỂN FORM PANEL ===
    function openFormPanel() { formPanel.classList.add("active"); }
    function closeFormPanel() { formPanel.classList.remove("active"); }

    // === CÁC HÀM XỬ LÝ SỰ KIỆN CRUD ===
    function handleAddClick() {
        closeFormPanel();
        setTimeout(() => {
            document.getElementById("employeeForm").reset();
            formTitle.textContent = "Thêm nhân viên mới";
            employeeIdInput.value = "";
            openFormPanel();
        }, 100);
    }

    function handleSaveEmployee() {
        const id = employeeIdInput.value;
        // Lấy và kiểm tra dữ liệu
        const name = employeeNameInput.value.trim();
        const position = employeePositionInput.value.trim();
        const salary = employeeSalaryInput.value;
        const phone = employeePhoneInput.value.trim();
        const email = employeeEmailInput.value.trim();
        const hireDate = employeeHireDateInput.value; // Giữ nguyên dạng YYYY-MM-DD
        const address = employeeAddressInput.value.trim();

        // Kiểm tra dữ liệu bắt buộc
        if (!name || !position || !salary || !email) {
            alert("Vui lòng nhập đầy đủ Tên, Chức vụ, Lương và Email.");
            return;
        }
        // Kiểm tra định dạng email đơn giản
        if (!/\S+@\S+\.\S+/.test(email)) {
             alert("Định dạng email không hợp lệ.");
             return;
        }
        // Kiểm tra lương là số dương
        if (parseFloat(salary) <= 0) {
            alert("Lương phải là một số dương.");
            return;
        }


        const employeeData = { name, position, salary, phone, email, hireDate, address };

        if (id) {
            updateEmployee(id, employeeData);
        } else {
            addEmployee(employeeData);
        }
    }

    function handleDeleteClick(id) {
        if (confirm(`Bạn có chắc muốn xóa nhân viên ID: ${id}?`)) {
            deleteEmployee(id);
        }
    }

    function handleEditClick(id) {
        closeFormPanel();
        setTimeout(() => {
            fetch(`http://localhost:8080/employee/${id}`) // GET không cần CSRF
                .then((response) => response.json())
                .then((employee) => {
                    formTitle.textContent = "Sửa thông tin nhân viên";
                    employeeIdInput.value = employee.id;
                    employeeNameInput.value = employee.name;
                    employeePositionInput.value = employee.position;
                    employeeSalaryInput.value = employee.salary;
                    employeePhoneInput.value = employee.phone;
                    employeeEmailInput.value = employee.email;
                    // API trả về date dạng YYYY-MM-DD, input type="date" nhận dạng này
                    employeeHireDateInput.value = employee.hireDate;
                    employeeAddressInput.value = employee.address;
                    openFormPanel();
                })
                .catch((error) => {
                    console.error("Lỗi khi lấy thông tin nhân viên:", error);
                    alert("Không thể tải thông tin nhân viên để sửa.");
                });
        }, 100);
    }

    // === CÁC HÀM GỌI API (CRUD) - ĐÃ THÊM CSRF ===
    function addEmployee(employeeData) {
        // SỬA 2: Thêm headers chứa CSRF token
        const headers = { "Content-Type": "application/json" };
        if (csrfHeader && csrfToken) headers[csrfHeader] = csrfToken;
        else { console.error("CSRF Headers not found for POST request."); return; }

        fetch("http://localhost:8080/employee", {
            method: "POST",
            headers: headers, // <-- Gửi token
            body: JSON.stringify(employeeData),
        })
        .then((response) => {
             if (!response.ok) {
                 // Đọc lỗi từ server nếu có
                 return response.text().then(text => { throw new Error(text || 'Thêm mới thất bại!'); });
            }
            closeFormPanel();
            fetchAllEmployees();
        })
        .catch((error) => {
            console.error("Lỗi khi thêm nhân viên:", error);
            alert(`Thêm mới thất bại: ${error.message}`);
        });
    }

    function updateEmployee(id, employeeData) {
        // SỬA 2: Thêm headers chứa CSRF token
        const headers = { "Content-Type": "application/json" };
         if (csrfHeader && csrfToken) headers[csrfHeader] = csrfToken;
        else { console.error("CSRF Headers not found for PUT request."); return; }

        fetch(`http://localhost:8080/employee/${id}`, {
            method: "PUT",
            headers: headers, // <-- Gửi token
            body: JSON.stringify(employeeData),
        })
        .then((response) => {
             if (!response.ok) {
                 return response.text().then(text => { throw new Error(text || 'Cập nhật thất bại!'); });
            }
            closeFormPanel();
            fetchAllEmployees();
        })
        .catch((error) => {
             console.error("Lỗi khi cập nhật nhân viên:", error);
            alert(`Cập nhật thất bại: ${error.message}`);
        });
    }

    function deleteEmployee(id) {
         // SỬA 2: Thêm headers chứa CSRF token
        const headers = {};
         if (csrfHeader && csrfToken) headers[csrfHeader] = csrfToken;
        else { console.error("CSRF Headers not found for DELETE request."); return; }

        fetch(`http://localhost:8080/employee/${id}`, {
            method: "DELETE",
             headers: headers // <-- Gửi token
        })
        .then((response) => {
             if (!response.ok) {
                 // Đọc lỗi từ server
                return response.text().then(text => {
                    // Kiểm tra lỗi khóa ngoại (nếu Employee có liên quan đến Order chẳng hạn)
                    if(text.includes("SQLIntegrityConstraintViolationException")){
                        alert("Xóa thất bại! Nhân viên này có thể đang liên kết với các hóa đơn.");
                    } else {
                         try {
                             const errorJson = JSON.parse(text);
                             alert(`Xóa thất bại: ${errorJson.message || 'Lỗi không xác định.'}`);
                        } catch(e) {
                             alert(`Xóa thất bại: ${text || 'Lỗi không xác định.'}`);
                        }
                    }
                    throw new Error('Xóa thất bại!');
                });
            }
            fetchAllEmployees(); // Chỉ gọi khi thành công
        })
        .catch((error) => console.error("Lỗi khi xóa nhân viên:", error.message)); // Log message lỗi
    }

    // === CÁC HÀM TÌM KIẾM VÀ HIỂN THỊ ===
    function handleSearchTypeChange() {
        const type = this.value;
        const keywordGroup = document.getElementById("keyword-input-group");
        const searchButtonsGroup = document.getElementById("search-buttons");

        if (type) {
            keywordGroup.classList.remove("hidden");
            searchButtonsGroup.classList.remove("hidden");
            // Cập nhật placeholder
             if(type === 'id') searchInput.placeholder = 'Nhập ID nhân viên...';
            else if (type === 'nameOrEmail') searchInput.placeholder = 'Nhập tên hoặc email...';
            else if (type === 'phone') searchInput.placeholder = 'Nhập số điện thoại...';
        } else {
            keywordGroup.classList.add("hidden");
            searchButtonsGroup.classList.add("hidden");
        }
    }

    function performSearch() {
        const searchType = searchTypeSelect.value; // 'id', 'nameOrEmail', 'phone'
        const keyword = searchInput.value.trim();

        if (!keyword) {
            resultContainer.innerHTML = "<p>⚠️ Vui lòng nhập từ khóa tìm kiếm!</p>";
            return;
        }

        let apiUrl = "";
        // Logic này của bạn đã đúng, khớp với Controller
        if (searchType === "id") {
            apiUrl = `http://localhost:8080/employee/${encodeURIComponent(keyword)}`;
        } else if (searchType === "nameOrEmail") {
             // API endpoint là /search?keyword=...
            apiUrl = `http://localhost:8080/employee/search?keyword=${encodeURIComponent(keyword)}`;
        } else if (searchType === "phone") {
             // API endpoint là /phone?keyword=... (Đã sửa ở Controller trước đó)
            apiUrl = `http://localhost:8080/employee/phone?keyword=${encodeURIComponent(keyword)}`; // Giả sử controller dùng keyword
             // Nếu controller dùng /phone?phone=... thì đổi ở đây
             // apiUrl = `http://localhost:8080/employee/phone?phone=${encodeURIComponent(keyword)}`;
        } else {
             resultContainer.innerHTML = "<p>⚠️ Loại tìm kiếm không hợp lệ!</p>";
             return;
        }

        fetch(apiUrl) // GET không cần CSRF
            .then((response) => {
                if (!response.ok) throw new Error(`Không tìm thấy hoặc lỗi: ${response.status}`);
                return response.json();
            })
            .then((data) => {
                const dataArray = Array.isArray(data) ? data : (data ? [data] : []);
                displayEmployees(dataArray, `Kết quả cho "${keyword}"`);
            })
            .catch((error) => {
                console.error("Lỗi tìm kiếm:", error);
                resultContainer.innerHTML = `<h3>Kết quả cho "${keyword}"</h3><p>❌ Không tìm thấy nhân viên nào khớp (${error.message}).</p>`;
            });
    }

    function fetchAllEmployees() {
        searchTypeSelect.value = "id"; // Đặt lại mặc định
        searchInput.value = "";
        handleSearchTypeChange.call(searchTypeSelect);
        resultContainer.innerHTML = "<p>Đang tải danh sách nhân viên...</p>";

        fetch("http://localhost:8080/employee") // GET không cần CSRF
            .then((response) => {
                if (!response.ok) throw new Error("Lỗi khi gọi API");
                return response.json();
            })
            .then((data) => displayEmployees(data, "Tất cả nhân viên"))
            .catch((error) => {
                console.error("Lỗi tải danh sách:", error);
                resultContainer.innerHTML = "<p style='color:red;'>❌ Không thể tải danh sách nhân viên!</p>";
            });
    }

    function displayEmployees(employees, title) {
        if (!employees || employees.length === 0) {
            resultContainer.innerHTML = `<h3>${title}</h3><p>❌ Không tìm thấy nhân viên nào khớp!</p>`;
            return;
        }
        let html = `<h3>${title} (${employees.length} kết quả)</h3>`;
        html += `
            <table>
                <thead>
                    <tr>
                        <th>ID</th><th>Tên nhân viên</th><th>Chức vụ</th><th>Lương (VND)</th>
                        <th>Số di động</th><th>Email</th><th>Ngày vào làm</th><th>Địa chỉ</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
        `;
        employees.forEach((employee) => {
             // Định dạng lương cho đẹp
             const formattedSalary = employee.salary ? parseFloat(employee.salary).toLocaleString('vi-VN') : 'N/A';
            html += `
                <tr>
                    <td>${employee.id}</td>
                    <td><strong>${employee.name ?? 'N/A'}</strong></td>
                    <td>${employee.position ?? 'N/A'}</td>
                    <td>${formattedSalary}</td>
                    <td>${employee.phone ?? 'N/A'}</td>
                    <td>${employee.email ?? 'N/A'}</td>
                    <td>${employee.hireDate ?? 'N/A'}</td>
                    <td>${employee.address ?? 'N/A'}</td>
                    <td class="action-buttons">
                        <button class="edit-btn" data-id="${employee.id}" title="Sửa">
                           <i class="fas fa-pen"></i>
                        </button>
                        <button class="delete-btn" data-id="${employee.id}" title="Xóa">
                           <i class="fas fa-trash"></i>
                        </button>
                    </td>
                </tr>
            `;
        });
        html += `</tbody></table>`;
        resultContainer.innerHTML = html;
    }
});
