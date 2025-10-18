document.addEventListener("DOMContentLoaded", function () {
    // === LẤY CÁC PHẦN TỬ TRÊN TRANG ===
    const searchTypeSelect = document.getElementById("searchType");
    const searchDetails = document.getElementById("search-details");
    const keywordInputGroup = document.getElementById("keyword-input-group");
    const searchInput = document.getElementById("searchInput");
    const priceRangeInputs = document.getElementById("price-range-inputs");
    const minPriceInput = document.getElementById("minPrice");
    const maxPriceInput = document.getElementById("maxPrice");
    const searchButtons = document.getElementById("search-buttons");
    const searchButton = document.getElementById("searchButton");
    const showAllButton = document.getElementById("showAllButton");
    const resultContainer = document.getElementById("result");

    const formPanel = document.getElementById("productFormPanel");
    const formTitle = document.getElementById("formTitle");
    const addProductBtn = document.getElementById("addProductBtn");
    const saveProductBtn = document.getElementById("saveProductBtn");
    const closeFormBtn = document.getElementById("closeFormBtn");
    
    const productIdInput = document.getElementById("productId");
    const productNameInput = document.getElementById("productName");
    const productPriceInput = document.getElementById("productPrice");
    const productQuantityInput = document.getElementById("productQuantity");
    const productDescriptionInput = document.getElementById("productDescription");
    const productCategorySelect = document.getElementById("productCategory");

    // === GẮN CÁC SỰ KIỆN ===
    fetchAllProducts();

    searchTypeSelect.addEventListener('change', handleSearchTypeChange);
    searchButton.addEventListener("click", performSearch);
    showAllButton.addEventListener("click", fetchAllProducts);
    addProductBtn.addEventListener("click", handleAddClick);
    saveProductBtn.addEventListener("click", handleSaveProduct);
    closeFormBtn.addEventListener("click", closeFormPanel);

    resultContainer.addEventListener('click', function(event) {
        const target = event.target;
        if (target.classList.contains('edit-btn')) handleEditClick(target.dataset.id);
        if (target.classList.contains('delete-btn')) handleDeleteClick(target.dataset.id);
    });
    
    // === CÁC HÀM ĐIỀU KHIỂN GIAO DIỆN ===
    function openFormPanel() {
        formPanel.classList.add('active'); // Thêm class 'active' để trượt xuống
    }
    function closeFormPanel() {
        formPanel.classList.remove('active'); // Xóa class 'active' để trượt lên
    }

    // === CÁC HÀM XỬ LÝ SỰ KIỆN ===
    function handleAddClick() {
        closeFormPanel();
        setTimeout(() => {
            document.getElementById("productForm").reset();
            formTitle.textContent = "Thêm Sản Phẩm Mới";
            productIdInput.value = "";
            loadCategories();
            openFormPanel();
        }, 100);
    }

    function handleEditClick(id) {
        closeFormPanel();
        setTimeout(() => {
            fetch(`http://localhost:8080/product/${id}`)
                .then(response => response.json())
                .then(product => {
                    formTitle.textContent = "Sửa Thông Tin Sản Phẩm";
                    productIdInput.value = product.id;
                    productNameInput.value = product.name;
                    productPriceInput.value = product.price;
                    productQuantityInput.value = product.quantity;
                    productDescriptionInput.value = product.description;
                    loadCategories(product.category.name);
                    openFormPanel();
                })
                .catch(error => console.error("Lỗi:", error));
        }, 100);
    }

    function handleSaveProduct() {
        const id = productIdInput.value;
        const productData = {
            name: productNameInput.value,
            price: parseFloat(productPriceInput.value),
            quantity: parseInt(productQuantityInput.value),
            description: productDescriptionInput.value,
            categoryName: productCategorySelect.value
        };
        if (id) {
            updateProduct(id, productData);
        } else {
            addProduct(productData);
        }
    }

    function handleDeleteClick(id) {
        if (confirm(`Bạn có chắc muốn xóa sản phẩm ID: ${id}?`)) {
            deleteProduct(id);
        }
    }

    // === CÁC HÀM GỌI API (CRUD) ===
    function addProduct(productData) {
        fetch('http://localhost:8080/product', { 
            method: 'POST', headers: { 'Content-Type': 'application/json' }, 
            body: JSON.stringify(productData) 
        })
        .then(response => { 
            if (!response.ok) throw new Error('Thêm mới thất bại!'); 
            closeFormPanel(); 
            fetchAllProducts(); 
        })
        .catch(error => alert('Thêm mới thất bại: ' + error.message));
    }

    function updateProduct(id, productData) {
        fetch(`http://localhost:8080/product/${id}`, { 
            method: 'PUT', headers: { 'Content-Type': 'application/json' }, 
            body: JSON.stringify(productData) 
        })
        .then(response => { 
            if (!response.ok) throw new Error('Cập nhật thất bại!'); 
            closeFormPanel(); fetchAllProducts(); 
        })
        .catch(error => alert('Cập nhật thất bại: ' + error.message));
    }

    function deleteProduct(id) {
        fetch(`http://localhost:8080/product/${id}`, { 
            method: 'DELETE' 
        })
        .then(response => { 
            if (!response.ok) throw new Error('Xóa thất bại!'); 
            fetchAllProducts(); 
        })
        .catch(error => alert('Xóa thất bại: ' + error.message));
    }

        // === CÁC HÀM ĐIỀU KHIỂN GIAO DIỆN TÌM KIẾM ===
    function handleSearchTypeChange() {
        const type = this.value;
        
        // Luôn ẩn tất cả các ô nhập liệu trước khi quyết định hiện cái nào
        keywordInputGroup.classList.add('hidden');
        priceRangeInputs.classList.add('hidden');
        
        // Reset lại ô input đơn
        searchInput.type = 'text';
        searchInput.placeholder = 'Nhập từ khóa...';
        keywordInputGroup.querySelector('label').textContent = 'Nhập từ khóa:';

        if (type === 'name' || type === 'category') {
            keywordInputGroup.classList.remove('hidden');
        } 
        else if (type === 'price_less') {
            keywordInputGroup.classList.remove('hidden');
            searchInput.type = 'number';
            searchInput.placeholder = 'Nhập giá tối đa...';
            keywordInputGroup.querySelector('label').textContent = 'Giá nhỏ hơn (VND):';
        } 
        else if (type === 'price_between') {
            priceRangeInputs.classList.remove('hidden');
        }
        
        // Hiện các nút bấm nếu đã chọn một tiêu chí
        if (type) {
            searchButtons.classList.remove('hidden');
        } else {
            searchButtons.classList.add('hidden');
        }
    }

    function loadCategories(selectedCategoryName = null) {
        fetch('http://localhost:8080/category')
            .then(response => response.json())
            .then(categories => {
                productCategorySelect.innerHTML = '<option value="">-- Chọn danh mục --</option>';
                categories.forEach(cat => {
                    const option = document.createElement('option');
                    option.value = cat.name;
                    option.textContent = cat.name;
                    if (cat.name === selectedCategoryName) {
                        option.selected = true;
                    }
                    productCategorySelect.appendChild(option);
                });
            });
    }

    // === CÁC HÀM GỌI API VÀ HIỂN THỊ ===
    function performSearch() {
        const searchType = searchTypeSelect.value;
        let apiUrl = '';
        let title = '';

        if (searchType === 'name' || searchType === 'category') {
            const keyword = searchInput.value.trim();
            if (!keyword) { 
                resultContainer.innerHTML = "<p>⚠️ Vui lòng nhập từ khóa!</p>"; return; 
            }
            apiUrl = `http://localhost:8080/product/${searchType}?keyword=${encodeURIComponent(keyword)}`;
            title = `Kết quả cho "${keyword}"`;
        } 
        else if (searchType === 'price_less') {
            const max = searchInput.value;
            if (!max) { resultContainer.innerHTML = "<p>⚠️ Vui lòng nhập giá tối đa!</p>"; return; }
            apiUrl = `http://localhost:8080/product/price/less?max=${max}`;
            title = `Sản phẩm có giá < ${parseFloat(max).toLocaleString('vi-VN')} VND`;
        } 
        else if (searchType === 'price_between') {
            const min = minPriceInput.value;
            const max = maxPriceInput.value;
            if (!min || !max) { resultContainer.innerHTML = "<p>⚠️ Vui lòng nhập khoảng giá!</p>"; return; }
            apiUrl = `http://localhost:8080/product/price/between?min=${min}&max=${max}`;
            title = `Sản phẩm có giá từ ${parseFloat(min).toLocaleString('vi-VN')} đến ${parseFloat(max).toLocaleString('vi-VN')} VND`;
        }

        if (!apiUrl) return;

        fetch(apiUrl)
            .then(response => {
                if (!response.ok) throw new Error(`Lỗi Status: ${response.status}`);
                return response.json();
            })
            .then(data => displayProducts(data, title))
            .catch(error => { console.error("Lỗi:", error); resultContainer.innerHTML = `<h3>${title}</h3><p>❌ Không tìm thấy sản phẩm nào khớp.</p>`; });
    }

    function fetchAllProducts() {
        searchTypeSelect.value = "";
        handleSearchTypeChange.call(searchTypeSelect); 
        resultContainer.innerHTML = "<p>Đang tải danh sách sản phẩm...</p>";
        fetch('http://localhost:8080/product')
            .then(response => {
                if (!response.ok) throw new Error("Lỗi khi gọi API");
                return response.json();
            })
            .then(data => displayProducts(data, "Tất cả sản phẩm"))
            .catch(error => { console.error("Lỗi:", error); resultContainer.innerHTML = "<p style='color:red;'>❌ Lỗi tải danh sách!</p>"; });
    }

    function displayProducts(data, title) {
        if (!data || data.length === 0) {
            resultContainer.innerHTML = `<h3>${title}</h3><p>❌ Không có sản phẩm nào trong danh sách.</p>`;
            return;
        }

        let html = `<h3>${title} (${data.length} kết quả)</h3>`;
        html += `
            <table>
                <thead>
                    <tr>
                        <th>ID</th><th>Tên sản phẩm</th><th>Giá (VND)</th>
                        <th>Số lượng</th><th>Mô tả</th><th>Danh mục</th> 
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
        `;
        data.forEach(product => {
            const categoryName = product.category ? product.category.name : 'N/A';
            html += `
                <tr>
                    <td>${product.id}</td>
                    <td><strong>${product.name}</strong></td>
                    <td>${product.price.toLocaleString('vi-VN')}</td>
                    <td>${product.quantity}</td>
                    <td>${product.description || 'N/A'}</td>
                    <td>${categoryName}</td>
                    <td class="action-buttons">
                        <button class="edit-btn" data-id="${product.id}">Sửa</button>
                        <button class="delete-btn" data-id="${product.id}">Xóa</button>
                    </td>
                </tr>
            `;
        });
        html += `</tbody></table>`;
        resultContainer.innerHTML = html;
    }
});
