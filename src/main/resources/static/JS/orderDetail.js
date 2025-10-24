document.addEventListener('DOMContentLoaded', () => {

    const API_BASE_URL = 'http://localhost:8080/orders';
    
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('id');

    const tabs = document.querySelectorAll('.tab');
    const tabContents = document.querySelectorAll('.tab-content');

    const orderIdDisplay = document.getElementById('orderIdDisplay');
    const customerName = document.getElementById('customerName');
    const orderDate = document.getElementById('orderDate');
    const orderStatus = document.getElementById('orderStatus');
    const employeeName = document.getElementById('employeeName');
    const totalAmount = document.getElementById('totalAmount');

    const itemTableBody = document.querySelector('#itemTable tbody');
    const addProductForm = document.getElementById('addProductForm');
    const productIdInput = document.getElementById('productIdInput');
    const quantityInput = document.getElementById('quantityInput');

    if (!orderId) {
        alert('Không tìm thấy ID đơn hàng!');
        window.location.href = '/page_order';
        return;
    }

    async function loadOrderData() {
        try {
            const response = await fetch(`${API_BASE_URL}/${orderId}`);
            if (!response.ok) throw new Error('Không thể tải thông tin đơn hàng.');
            const order = await response.json();
            orderIdDisplay.textContent = order.id;
            customerName.textContent = order.customer ? order.customer.name : 'N/A';
            employeeName.textContent = order.employee ? order.employee.name : 'N/A';
            orderDate.textContent = order.orderDate;
            orderStatus.textContent = order.status;
            totalAmount.textContent = formatCurrency(order.totalAmount);

        } catch (error) {
            console.error('Lỗi tải thông tin:', error);
            alert(error.message);
        }
    }

    async function loadOrderItems() {
        try {
            const response = await fetch(`${API_BASE_URL}/${orderId}/details`);
            if (!response.ok) throw new Error('Không thể tải danh sách sản phẩm.');
            
            const items = await response.json();
            renderItemTable(items);

        } catch (error) {
            console.error('Lỗi tải sản phẩm:', error);
            alert(error.message);
        }
    }

    function renderItemTable(items) {
        itemTableBody.innerHTML = ''; 
        if (items.length === 0) {
            itemTableBody.innerHTML = '<tr><td colspan="6" style="text-align:center;">Đơn hàng này chưa có sản phẩm.</td></tr>';
            return;
        }

        items.forEach(item => {
            const product = item.product || { id: 'N/A', name: 'Không rõ' }; 
            const subtotal = (item.quantity || 0) * (item.price || 0);

            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${product.id}</td>
                <td>${product.name}</td>
                <td>
                    <input type="number" class="quantity-update-input" value="${item.quantity}" 
                           min="1" data-detail-id="${item.id}" style="width: 60px;">
                </td>
                <td>${formatCurrency(item.price)}</td>
                <td>${formatCurrency(subtotal)}</td>
                <td class="action-buttons">
                    <button class="btn-update" data-detail-id="${item.id}">Cập nhật</button>
                    <button class="btn-delete" data-detail-id="${item.id}">Xóa</button>
                </td>
            `;
            itemTableBody.appendChild(row);
        });
    }

    async function handleAddProduct(event) {
        event.preventDefault();
        const productId = productIdInput.value.trim();
        const quantity = parseInt(quantityInput.value);

        if (!productId || !quantity || quantity <= 0) {
            alert('Vui lòng nhập Mã SP và Số lượng hợp lệ.');
            return;
        }

        const detailDTO = { productId, quantity };

        try {
            const response = await fetch(`${API_BASE_URL}/${orderId}/details`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(detailDTO)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Thêm thất bại: ${errorText}`);
            }

            alert('Thêm sản phẩm thành công!');
            addProductForm.reset();
            refreshAllData(); 
        } catch (error) {
            console.error('Lỗi thêm sản phẩm:', error);
            alert(error.message);
        }
    }

    async function handleUpdateQuantity(detailId, newQuantity) {
        if (!newQuantity || newQuantity <= 0) {
            alert('Số lượng phải lớn hơn 0. Nếu muốn xóa, vui lòng nhấn nút Xóa.');
            return;
        }
        
        const detailDTO = { quantity: newQuantity }; 

        try {
            const response = await fetch(`${API_BASE_URL}/details/${detailId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(detailDTO)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Cập nhật thất bại: ${errorText}`);
            }

            alert('Cập nhật số lượng thành công.');
            refreshAllData(); 
        } catch (error) {
            console.error('Lỗi cập nhật:', error);
            alert(error.message);
        }
    }

    async function handleDeleteDetail(detailId) {
        if (!confirm('Bạn có chắc chắn muốn xóa sản phẩm này khỏi đơn hàng?')) {
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/details/${detailId}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Xóa thất bại: ${errorText}`);
            }

            alert('Xóa sản phẩm thành công.');
            refreshAllData(); 
        } catch (error) {
            console.error('Lỗi xóa:', error);
            alert(error.message);
        }
    }

    function refreshAllData() {
        loadOrderData();
        loadOrderItems();
    }

    function formatCurrency(amount) {
        if (typeof amount !== 'number') amount = 0;
        return amount.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
    }

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {

            tabs.forEach(t => t.classList.remove('active'));
            tabContents.forEach(c => c.style.display = 'none');

            tab.classList.add('active');
            const tabName = tab.getAttribute('data-tab');
            document.getElementById(`${tabName}Content`).style.display = 'block';
        });
    });

    itemTableBody.addEventListener('click', (event) => {
        const target = event.target;
        const detailId = target.dataset.detailId;

        if (target.classList.contains('btn-delete')) {
            handleDeleteDetail(detailId);
        }

        if (target.classList.contains('btn-update')) {
            const input = itemTableBody.querySelector(`input.quantity-update-input[data-detail-id="${detailId}"]`);
            const newQuantity = parseInt(input.value);
            handleUpdateQuantity(detailId, newQuantity);
        }
    });

    addProductForm.addEventListener('submit', handleAddProduct);
    refreshAllData(); 

});