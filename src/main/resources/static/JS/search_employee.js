document.addEventListener('DOMContentLoaded', function () {
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

    fetchAllEmployees();

    searchTypeSelect.addEventListener('change', handleSearchTypeChange);
    searchButton.addEventListener("click", performSearch);
    showAllButton.addEventListener("click", fetchAllEmployees);
    addEmployeeBtn.addEventListener("click", handleAddClick);
    saveEmployeeBtn.addEventListener("click", handleSaveEmployee);
    closeFormBtn.addEventListener("click", closeFormPanel);

    const dropdownToggle = document.querySelector('.dropdown-toggle');
    const dropdown = document.querySelector('.dropdown');

    if(dropdownToggle && dropdown) {
        dropdownToggle.addEventListener('click', function(event){
            event.preventDefault();
            dropdown.classList.toggle('active');
        });
    }

    window.addEventListener(
        'click', 
        function(e){
            if(dropdown && !dropdown.contains(e.target) && !dropdownToggle.contains(e.target)) {
                dropdown.classList.remove('active');
            }
        }
    );

    resultContainer.addEventListener(
            'click', 
            function(event) {
            const target = event.target;
            if (target.classList.contains('edit-btn')) {
                handleEditClick(target.dataset.id);
            }
            if (target.classList.contains('delete-btn')) {
                handleDeleteClick(target.dataset.id);
            }
        }
    );

    

    function openFormPanel() {
        formPanel.classList.add('active'); 
    }
    function closeFormPanel() {
        formPanel.classList.remove('active'); 
    }

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
        const employeeData = {
            name: employeeNameInput.value,
            position: employeePositionInput.value,
            salary: employeeSalaryInput.value,
            phone: employeePhoneInput.value,
            email: employeeEmailInput.value,
            hireDate: employeeHireDateInput.value,
            address: employeeAddressInput.value
        };
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
            fetch(`http://localhost:8080/employee/${id}`)
                .then(response => response.json())
                .then(employee => {
                    formTitle.textContent = "Sửa thông tin nhân viên";
                    employeeIdInput.value = employee.id;
                    employeeNameInput.value = employee.name;
                    employeePositionInput.value = employee.position;
                    employeeSalaryInput.value = employee.salary;
                    employeePhoneInput.value = employee.phone;
                    employeeEmailInput.value = employee.email;
                    employeeHireDateInput.value = employee.hireDate;
                    employeeAddressInput.value = employee.address;
                    openFormPanel();
                })
                .catch(error => console.error("Lỗi:", error));
        }, 100);
    }

    function addEmployee(employeeData) {
        fetch('http://localhost:8080/employee', { 
            method: 'POST', headers: { 'Content-Type': 'application/json' }, 
            body: JSON.stringify(employeeData) 
        })
        .then(response => { 
            if (!response.ok) throw new Error('Thêm mới thất bại!'); 
            closeFormPanel(); 
            fetchAllEmployees(); 
        })
        .catch(error => console.error("Lỗi:", error));
    }

    function updateEmployee(id, employeeData) {
        fetch(`http://localhost:8080/employee/${id}`, { 
            method: 'PUT', headers: { 'Content-Type': 'application/json' }, 
            body: JSON.stringify(employeeData) 
        })
        .then(response => { 
            if (!response.ok) throw new Error('Cập nhật thất bại!'); 
            closeFormPanel(); fetchAllEmployees(); 
        })
        .catch(error => console.error("Lỗi:", error));
    }

    function deleteEmployee(id) {
        fetch(`http://localhost:8080/employee/${id}`, { 
            method: 'DELETE' 
        })
        .then(response => { 
            if (!response.ok) throw new Error('Xóa thất bại!'); 
            fetchAllEmployees(); 
        })
        .catch(error => console.error("Lỗi:", error));
    }

    function handleSearchTypeChange() {
        const type = this.value; 

        const keywordGroup = document.getElementById('keyword-input-group');
        const searchButtonsGroup = document.getElementById('search-buttons');

        if (type) {
            keywordGroup.classList.remove('hidden');
            searchButtonsGroup.classList.remove('hidden');
        } 
        else {
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
            apiUrl = `http://localhost:8080/employee/${encodeURIComponent(keyword)}`;
        } 
        else if (searchType === 'nameOrEmail'){ 
            apiUrl = `http://localhost:8080/employee/search?keyword=${encodeURIComponent(keyword)}`;
        }
        else if (searchType === 'phone'){
            apiUrl = `http://localhost:8080/employee/phone?keyword=${encodeURIComponent(keyword)}`;
        }

        fetch(apiUrl)
        .then(response => {
            if (!response.ok) throw new Error("Lỗi khi gọi API");
            return response.json();
        })
        .then(data => {
            const dataArray = Array.isArray(data) ? data : (data ? [data] : []);
            displayEmployees(dataArray, `Kết quả cho "${keyword}"`);
        })
        .catch(error => {
            console.error("Lỗi:", error);
            resultContainer.innerHTML = `<h3>Kết quả cho "${keyword}"</h3><p>❌ Không tìm thấy danh mục sản phẩm nào khớp.</p>`;
        });
    }

    function fetchAllEmployees() {
        searchTypeSelect.value = "";
        handleSearchTypeChange.call(searchTypeSelect);
        resultContainer.innerHTML = "<p>Đang tải danh sách nhân viên...</p>";
        
        fetch('http://localhost:8080/employee')
        .then(response => {
            if (!response.ok) throw new Error("Lỗi khi gọi API");
            return response.json();
        })
        .then(data => displayEmployees(data, "Tất cả nhân viên"))
        .catch(error => {
            console.error("Lỗi:", error);
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
                        <th>ID</th>
                        <th>Tên nhân viên</th>
                        <th>Chức vụ</th>
                        <th>Lương</th>
                        <th>Số di động</th>
                        <th>Email</th>
                        <th>Ngày vào làm</th>
                        <th>Địa chỉ</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
        `;

        employees.forEach(employee => {
            html += `
                <tr>
                    <td>${employee.id}</td>
                    <td>${employee.name}</td>
                    <td>${employee.position || 'N/A'}</td>
                    <td>${employee.salary || 'N/A'}</td>
                    <td>${employee.phone || 'N/A'}</td>
                    <td>${employee.email || 'N/A'}</td>
                    <td>${employee.hireDate || 'N/A'}</td>
                    <td>${employee.address || 'N/A'}</td>
                    <td class="action-buttons">
                        <button class="edit-btn" data-id="${employee.id}" title="Sửa sản phẩm">
                            <i class="fas fa-pen"></i>
                        </button>
                        <button class="delete-btn" data-id="${employee.id}" title="Xóa sản phẩm">
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