document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('id');
    
    if (orderId) {
        document.getElementById('orderIdDisplay').textContent = orderId;
        fetchOrderData(orderId);
    } else {
        alert("Lỗi: Không tìm thấy Mã Đơn hàng!");
    }
    
    setupTabs();
});

const API_BASE_URL = 'http://localhost:8080/orders'; 

function formatCurrency(amount) {
    if (typeof amount !== 'number') return '0 VND';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}

function setupTabs() {
    document.querySelectorAll('.tab').forEach(tab => {
        tab.addEventListener('click', function() {
            // Loại bỏ active khỏi tất cả tabs
            document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
            // Thêm active cho tab được click
            this.classList.add('active');

            // Ẩn tất cả nội dung
            document.querySelectorAll('.tab-content').forEach(content => content.style.display = 'none');
            
            // Hiện nội dung tương ứng
            const tabName = this.getAttribute('data-tab');
            document.getElementById(`${tabName}Content`).style.display = 'block';
        });
    });
}

async function fetchOrderData(orderId) {
    try {
        // 1. Lấy thông tin đơn hàng
        const orderResponse = await fetch(`${API_BASE_URL}/${orderId}`);
        const order = await orderResponse.json();
        renderOrderInfo(order);

        // 2. Lấy chi tiết hàng hóa
        const detailsResponse = await fetch(`${API_BASE_URL}/${orderId}/details`);
        const details = await detailsResponse.json();
        renderOrderDetails(details);

    } catch (error) {
        console.error("Lỗi tải chi tiết đơn hàng:", error);
        alert("Không thể tải chi tiết đơn hàng. Vui lòng kiểm tra ID và Server.");
    }
}

function renderOrderInfo(order) {
    // Cập nhật thông tin chung
    document.getElementById('customerName').textContent = order.customer ? order.customer.name : 'N/A';
    document.getElementById('orderDate').textContent = order.orderDate || 'N/A';
    document.getElementById('orderStatus').textContent = order.status || 'N/A';
    document.getElementById('employeeName').textContent = order.employee ? order.employee.name : 'N/A';
    document.getElementById('totalAmount').textContent = formatCurrency(order.totalAmount);
}

function renderOrderDetails(details) {
    const tableBody = document.querySelector('#itemTable tbody');
    tableBody.innerHTML = '';
    let totalQuantity = 0;
    let totalItemsAmount = 0;

    details.forEach(detail => {
        const row = tableBody.insertRow();
        const subtotal = detail.price * detail.quantity;
        totalItemsAmount += subtotal;
        totalQuantity += detail.quantity;

        // Giả sử Product Entity có trường id, name, price
        row.insertCell().textContent = detail.product ? detail.product.id : 'N/A';
        row.insertCell().textContent = detail.product ? detail.product.name : 'N/A'; 
        row.insertCell().textContent = detail.quantity;
        row.insertCell().textContent = formatCurrency(detail.price);
        row.insertCell().textContent = formatCurrency(subtotal);
    });

    // Hàng tổng cộng (mô phỏng ảnh)
    const totalRow = tableBody.insertRow();
    totalRow.style.fontWeight = 'bold';
    totalRow.style.backgroundColor = '#e9ecef';
    
    // 3 cột trống cho Mã, Diễn giải, Đơn giá
    totalRow.insertCell().textContent = ''; 
    totalRow.insertCell().textContent = 'Tổng cộng'; 
    totalRow.insertCell().textContent = totalQuantity; // Tổng số lượng
    totalRow.insertCell().textContent = ''; 
    totalRow.insertCell().textContent = formatCurrency(totalItemsAmount); // Tổng thành tiền
}