document.addEventListener('DOMContentLoaded', function () {
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

    fetchAllCategories();

    searchTypeSelect.addEventListener('change', handleSearchTypeChange);
    searchButton.addEventListener("click", performSearch);
    showAllButton.addEventListener("click", fetchAllCategories);
    addCategoryBtn.addEventListener("click", handleAddClick);
    saveCategoryBtn.addEventListener("click", handleSaveCategory);
    closeFormBtn.addEventListener("click", closeFormPanel);

    resultContainer.addEventListener('click', function(event) {
        const target = event.target;
        if (target.classList.contains('edit-btn')) {
            handleEditClick(target.dataset.id);
        }
        if (target.classList.contains('delete-btn')) {
            handleDeleteClick(target.dataset.id);
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
            document.getElementById("categoryForm").reset();
            formTitle.textContent = "Thêm danh mục mới";
            categoryIdInput.value = "";
            openFormPanel();
        }, 100);
    }

    function handleSaveCategory() {
        const id = categoryIdInput.value;
        const categoryData = {
            name: categoryNameInput.value,
        };
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
        closeFormPanel(); // Đóng form cũ nếu đang mở
        setTimeout(() => {
            fetch(`http://localhost:8080/category/${id}`)
                .then(response => response.json())
                .then(category => {
                    formTitle.textContent = "Sửa thông tin danh mục sản phẩm";
                    categoryIdInput.value = category.id;
                    categoryNameInput.value = category.name;
                    openFormPanel();
                })
                .catch(error => console.error("Lỗi:", error));
        }, 100);
    }

    function addCategory(categoryData) {
        fetch('http://localhost:8080/category', { 
            method: 'POST', headers: { 'Content-Type': 'application/json' }, 
            body: JSON.stringify(categoryData) 
        })
        .then(response => { 
            if (!response.ok) throw new Error('Thêm mới thất bại!'); 
            closeFormPanel(); 
            fetchAllCategories(); 
        })
        .catch(error => console.error("Lỗi:", error));
    }

    function updateCategory(id, categoryData) {
        fetch(`http://localhost:8080/category/${id}`, { 
            method: 'PUT', headers: { 'Content-Type': 'application/json' }, 
            body: JSON.stringify(categoryData) 
        })
        .then(response => { 
            if (!response.ok) throw new Error('Cập nhật thất bại!'); 
            closeFormPanel(); fetchAllCategories(); 
        })
        .catch(error => console.error("Lỗi:", error));
    }

    function deleteCategory(id) {
        fetch(`http://localhost:8080/category/${id}`, { 
            method: 'DELETE' 
        })
        .then(response => { 
            if (!response.ok) throw new Error('Xóa thất bại!'); 
            fetchAllCategories(); 
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
            apiUrl = `http://localhost:8080/category/${encodeURIComponent(keyword)}`;
        }

        fetch(apiUrl)
        .then(response => {
            if (!response.ok) throw new Error("Lỗi khi gọi API");
            return response.json();
        })
        .then(data => {
            const dataArray = Array.isArray(data) ? data : (data ? [data] : []);
            displayCategories(dataArray, `Kết quả cho "${keyword}"`);
        })
        .catch(error => {
            console.error("Lỗi:", error);
            resultContainer.innerHTML = `<h3>Kết quả cho "${keyword}"</h3><p>❌ Không tìm thấy danh mục sản phẩm nào khớp.</p>`;
        });
    }

    function fetchAllCategories() {
        searchTypeSelect.value = "";
        handleSearchTypeChange.call(searchTypeSelect);
        resultContainer.innerHTML = "<p>Đang tải danh sách danh mục sản phẩm...</p>";
        
        fetch('http://localhost:8080/category')
        .then(response => {
            if (!response.ok) throw new Error("Lỗi khi gọi API");
            return response.json();
        })
        .then(data => displayCategories(data, "Tất cả danh mục sản phẩm"))
        .catch(error => {
            console.error("Lỗi:", error);
            resultContainer.innerHTML = "<p style='color:red;'>❌ Không thể tải danh sách danh mục sản phẩm!</p>";
        });     
    }
    
    function displayCategories(categories, title) {
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

        categories.forEach(category => {
            html += `
                <tr>
                    <td>${category.id}</td>
                    <td>${category.name}</td>
                    <td class="action-buttons">
                        <button class="edit-btn" data-id="${category.id}">Sửa</button>
                        <button class="delete-btn" data-id="${category.id}">Xóa</button>
                    </td>
                </tr>
            `;
        });
        html += `</tbody></table>`;
        resultContainer.innerHTML = html;
    }
});