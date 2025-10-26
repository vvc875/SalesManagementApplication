document.addEventListener("DOMContentLoaded", function () {
    // === LẤY CÁC PHẦN TỬ TRÊN TRANG ===
    const searchTypeSelect = document.getElementById("searchType");
    const keywordInputGroup = document.getElementById("keyword-input-group");
    const searchInput = document.getElementById("searchInput");
    const searchButton = document.getElementById("searchButton");
    const showAllButton = document.getElementById("showAllButton");
    const resultContainer = document.getElementById("result");

    const formPanel = document.getElementById("categoryFormPanel");
    const formTitle = document.getElementById("formTitle");
    const addCategoryBtn = document.getElementById("addCategoryBtn");
    const saveCategoryBtn = document.getElementById("saveCategoryBtn");
    const closeFormBtn = document.getElementById("closeFormBtn");

    const categoryNameInput = document.getElementById("categoryName");
    const categoryIdInput = document.getElementById("categoryId");

    /**
     * ===============================================
     * SỬA 1: ĐỌC CSRF TOKEN TỪ THẺ META
     * ===============================================
     */
    const csrfToken = document.querySelector("meta[name='_csrf']")?.getAttribute("content");
    const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.getAttribute("content");

    // Kiểm tra xem token có tồn tại không (để tránh lỗi nếu meta tag thiếu)
    if (!csrfToken || !csrfHeader) {
        console.error("CSRF token not found in meta tags!");
        // Có thể hiển thị lỗi cho người dùng ở đây
    }

    // === GẮN CÁC SỰ KIỆN ===
    fetchAllCategories(); // Tải danh sách ban đầu

    searchTypeSelect.addEventListener("change", handleSearchTypeChange);
    searchButton.addEventListener("click", performSearch);
    showAllButton.addEventListener("click", fetchAllCategories);
    addCategoryBtn.addEventListener("click", handleAddClick);
    saveCategoryBtn.addEventListener("click", handleSaveCategory);
    closeFormBtn.addEventListener("click", closeFormPanel);

    // Xử lý dropdown (code của bạn đã có)
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

    // Xử lý click nút Sửa/Xóa trong bảng (code của bạn đã có)
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
            document.getElementById("categoryForm").reset();
            formTitle.textContent = "Thêm danh mục mới";
            categoryIdInput.value = "";
            openFormPanel();
        }, 100);
    }

    function handleSaveCategory() {
        const id = categoryIdInput.value;
        const categoryData = { name: categoryNameInput.value };
        if (id) {
            updateCategory(id, categoryData);
        } else {
            addCategory(categoryData);
        }
    }

    function handleDeleteClick(id) {
        if (confirm(`Bạn có chắc muốn xóa danh mục sản phẩm ID: ${id}?`)) {
            deleteCategory(id);
        }
    }

    function handleEditClick(id) {
        closeFormPanel();
        setTimeout(() => {
            fetch(`http://localhost:8080/category/${id}`) // GET không cần CSRF
                .then((response) => response.json())
                .then((category) => {
                    formTitle.textContent = "Sửa thông tin danh mục sản phẩm";
                    categoryIdInput.value = category.id;
                    categoryNameInput.value = category.name;
                    openFormPanel();
                })
                .catch((error) => console.error("Lỗi:", error));
        }, 100);
    }

    // === CÁC HÀM GỌI API (CRUD) ===
    function addCategory(categoryData) {
        // SỬA 2: Thêm headers chứa CSRF token
        const headers = { "Content-Type": "application/json" };
        if (csrfHeader && csrfToken) headers[csrfHeader] = csrfToken;

        fetch("http://localhost:8080/category", {
            method: "POST",
            headers: headers, // <-- Gửi token
            body: JSON.stringify(categoryData),
        })
        .then((response) => {
            if (!response.ok) throw new Error("Thêm mới thất bại!");
            closeFormPanel();
            fetchAllCategories();
        })
        .catch((error) => console.error("Lỗi:", error.message));
    }

    function updateCategory(id, categoryData) {
        // SỬA 2: Thêm headers chứa CSRF token
        const headers = { "Content-Type": "application/json" };
        if (csrfHeader && csrfToken) headers[csrfHeader] = csrfToken;

        fetch(`http://localhost:8080/category/${id}`, {
            method: "PUT",
            headers: headers, // <-- Gửi token
            body: JSON.stringify(categoryData),
        })
        .then((response) => {
            if (!response.ok) throw new Error("Cập nhật thất bại!");
            closeFormPanel();
            fetchAllCategories();
        })
        .catch((error) => console.error("Lỗi:", error.message));
    }

    function deleteCategory(id) {
        // SỬA 2: Thêm headers chứa CSRF token
        const headers = {};
        if (csrfHeader && csrfToken) headers[csrfHeader] = csrfToken;

        fetch(`http://localhost:8080/category/${id}`, {
            method: "DELETE",
            headers: headers // <-- Gửi token
        })
        .then((response) => {
             if (!response.ok) {
                // Xử lý lỗi (ví dụ: lỗi khóa ngoại)
                return response.text().then(text => {
                    if(text.includes("SQLIntegrityConstraintViolationException")){
                        alert("Xóa thất bại! Danh mục này đang được sử dụng bởi một hoặc nhiều sản phẩm.");
                    } else if (text.includes("không tồn tại danh mục")){
                         alert("Xóa thất bại! Danh mục không tồn tại (có thể đã bị xóa trước đó).");
                    }
                    else {
                        alert("Xóa thất bại! Lỗi không xác định.");
                    }
                    throw new Error('Xóa thất bại!');
                });
            }
            fetchAllCategories(); // Chỉ gọi khi xóa thành công
        })
        .catch((error) => console.error("Lỗi:", error.message));
    }

    // === CÁC HÀM TÌM KIẾM VÀ HIỂN THỊ ===
    function handleSearchTypeChange() {
        // (Code này của bạn đã đúng)
        const type = this.value;
        const keywordGroup = document.getElementById("keyword-input-group");
        const searchButtonsGroup = document.getElementById("search-buttons");
        if (type) {
            keywordGroup.classList.remove("hidden");
            searchButtonsGroup.classList.remove("hidden");
        } else {
            keywordGroup.classList.add("hidden");
            searchButtonsGroup.classList.add("hidden");
        }
    }

    function performSearch() {
        const searchType = searchTypeSelect.value; // Lấy giá trị 'id' hoặc 'name'
        const keyword = searchInput.value.trim();

        if (!keyword) {
            resultContainer.innerHTML = "<p>⚠️ Vui lòng nhập từ khóa tìm kiếm!</p>";
            return;
        }

        let apiUrl = '';

        if (searchType === "id") {
            // Gọi API lấy theo ID
            apiUrl = `http://localhost:8080/category/${encodeURIComponent(keyword)}`;
        }
        /**
         * ===============================================
         * SỬA 3: BỔ SUNG LOGIC TÌM THEO TÊN
         * ===============================================
         */
        else if (searchType === "name") {
            // Gọi API tìm theo tên (sẽ tạo ở Controller)
            apiUrl = `http://localhost:8080/category/search?name=${encodeURIComponent(keyword)}`;
        }
        else {
             resultContainer.innerHTML = "<p>⚠️ Vui lòng chọn loại tìm kiếm hợp lệ!</p>";
            return;
        }


        fetch(apiUrl) // GET không cần CSRF
            .then((response) => {
                if (!response.ok) throw new Error("Lỗi khi gọi API hoặc không tìm thấy");
                return response.json();
            })
            .then((data) => {
                // Đảm bảo data luôn là mảng
                const dataArray = Array.isArray(data) ? data : (data ? [data] : []);
                displayCategories(dataArray, `Kết quả cho "${keyword}"`);
            })
            .catch((error) => {
                console.error("Lỗi:", error);
                resultContainer.innerHTML = `<h3>Kết quả cho "${keyword}"</h3><p>❌ Không tìm thấy danh mục sản phẩm nào khớp.</p>`;
            });
    }

    function fetchAllCategories() {
        searchTypeSelect.value = "name"; // Đặt lại giá trị mặc định khi hiển thị tất cả
        searchInput.value = ""; // Xóa từ khóa cũ
        handleSearchTypeChange.call(searchTypeSelect); // Cập nhật giao diện tìm kiếm
        resultContainer.innerHTML = "<p>Đang tải danh sách danh mục sản phẩm...</p>";

        fetch("http://localhost:8080/category") // GET không cần CSRF
            .then((response) => {
                if (!response.ok) throw new Error("Lỗi khi gọi API");
                return response.json();
            })
            .then((data) => displayCategories(data, "Tất cả danh mục sản phẩm"))
            .catch((error) => {
                console.error("Lỗi:", error);
                resultContainer.innerHTML = "<p style='color:red;'>❌ Không thể tải danh sách danh mục sản phẩm!</p>";
            });
    }

    function displayCategories(categories, title) {
        // (Code này của bạn đã đúng)
        if (!categories || categories.length === 0) {
            resultContainer.innerHTML = `<h3>${title}</h3><p>❌ Không tìm thấy danh mục sản phẩm nào.</p>`;
            return;
        }
        let html = `<h3>${title} (${categories.length} kết quả)</h3>`;
        html += `
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tên danh mục</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
        `;
        categories.forEach((category) => {
            html += `
                <tr>
                    <td>${category.id}</td>
                    <td>${category.name}</td>
                    <td class="action-buttons">
                        <button class="edit-btn" data-id="${category.id}" title="Sửa danh mục">
                            <i class="fas fa-pen"></i>
                        </button>
                        <button class="delete-btn" data-id="${category.id}" title="Xóa danh mục">
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
