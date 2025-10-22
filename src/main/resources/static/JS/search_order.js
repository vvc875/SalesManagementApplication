document.addEventListener('DOMContentLoaded', () => {
    fetchOrders();
    // Gán sự kiện cho nút Tạo Đơn hàng
    document.getElementById('btnCreateOrder').addEventListener('click', handleCreateOrder);
});

const API_BASE_URL = 'http://localhost:8080/orders'; 

function formatCurrency(amount) {
    if (typeof amount !== 'number') return '0 VND';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}

// 1. Tải danh sách đơn hàng
async function fetchOrders() {
    try {
        const response = await fetch(API_BASE_URL);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const orders = await response.json();
        renderOrderTable(orders);
        document.getElementById('total-results').textContent = orders.length; // Cập nhật tổng kết quả
    } catch (error) {
        console.error("Lỗi tải danh sách đơn hàng:", error);
        document.querySelector('#orderTable tbody').innerHTML = `<tr><td colspan="6" style="text-align: center; color: red;">Không thể tải dữ liệu. Vui lòng kiểm tra Server.</td></tr>`;
    }
}

// 2. Render bảng
function renderOrderTable(orders) {
    const tableBody = document.querySelector('#orderTable tbody');
    tableBody.innerHTML = ''; 
    const COLSPAN_COUNT = 7; // Cập nhật tổng số cột thành 7

    if (orders.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="${COLSPAN_COUNT}" style="text-align: center;">Không có đơn hàng nào.</td></tr>`;
        return;
    }

    orders.forEach(order => {
        const row = tableBody.insertRow();
        
        // 1. Mã ĐH (Link)
        row.insertCell().innerHTML = `<span class="order-id-link" onclick="viewOrderDetails('${order.id}')">${order.id}</span>`;
        
        // 2. Khách hàng
        // Dựa vào cấu trúc DB của bạn, tên khách hàng được truy cập qua order.customer.name
        row.insertCell().textContent = order.customer ? order.customer.name : 'Chưa rõ';
        
        // 3. Nhân viên bán hàng (CỘT MỚI)
        // Tên nhân viên được truy cập qua order.employee.name
        row.insertCell().textContent = order.employee ? order.employee.name : 'Chưa rõ';
        
        // 4. Ngày đặt
        row.insertCell().textContent = order.orderDate || 'N/A';
        
        // 5. Tổng tiền
        row.insertCell().textContent = formatCurrency(order.totalAmount);
        
        // 6. Trạng thái
        row.insertCell().textContent = order.status;
        
        // 7. Hành động
        const actionCell = row.insertCell();
        actionCell.innerHTML = `
            <button class="btn btn-detail" onclick="viewOrderDetails('${order.id}')" title="Xem chi tiết">
                <i class="fas fa-eye"></i>
            </button>
            <button class="btn btn-delete" onclick="handleDeleteOrder('${order.id}')" title="Xóa đơn hàng">
                <i class="fas fa-trash"></i>
            </button>
        `;
    });
}

// 3. Xử lý chuyển trang Chi tiết
function viewOrderDetails(orderId) {
    // Sửa lại tên file để khớp với order_detail.html
    window.location.href = `order_detail.html?id=${orderId}`;
}

// 4. Xử lý xóa đơn hàng
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
        fetchOrders(); // Tải lại danh sách sau khi xóa

    } catch (error) {
        console.error("Lỗi xóa đơn hàng:", error);
        alert(error.message);
    }
}

// 5. Xử lý Tạo Order Rỗng (Bước 1)
async function handleCreateOrder() {
    // Yêu cầu admin nhập ID khách hàng và nhân viên cho demo/mockup
    const customerId = prompt("Nhập ID Khách hàng (ví dụ: C001):");
    const employeeId = prompt("Nhập ID Nhân viên (ví dụ: E001):");

    if (!customerId || !employeeId) {
        alert("Vui lòng cung cấp đủ ID Khách hàng và Nhân viên.");
        return;
    }

    const orderData = { customerId, employeeId };

    try {
        const response = await fetch(API_BASE_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(orderData)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Lỗi khi tạo đơn hàng: ${errorText}`);
        }

        const newOrder = await response.json();
        const newOrderId = newOrder.id; 

        alert(`Đã tạo đơn hàng rỗng thành công với ID: ${newOrderId}. Bây giờ chuyển sang trang chi tiết để thêm sản phẩm.`);
        
        // Chuyển sang trang chi tiết (Bước 2: Thêm sản phẩm)
        viewOrderDetails(newOrderId); 

    } catch (error) {
        console.error("Lỗi tạo đơn hàng:", error);
        alert(error.message);
    }
}