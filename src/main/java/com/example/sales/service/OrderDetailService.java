package com.example.sales.service;

import com.example.sales.dto.OrderDetailRequestDTO;
import com.example.sales.entity.Order;
import com.example.sales.entity.OrderDetail;
import com.example.sales.entity.Product;
import com.example.sales.repository.OrderDetailRepository;
import com.example.sales.repository.OrderRepository;
import com.example.sales.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; // Import Optional
import java.util.UUID;

@Service
public class OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;

    // Sinh orderDetailId
    private String generateOrderDetailId() {
        long count = orderDetailRepository.count();
        return String.format("OD%03d", count + 1);
    }

    // Thêm sản phẩm vào đơn hàng
    @Transactional
    public OrderDetail addProductToOrder(String orderId, OrderDetailRequestDTO detailDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));
        Product product = productRepository.getProductById(detailDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));

        if (product.getQuantity() < detailDTO.getQuantity()) {
            throw new RuntimeException("Số lượng sản phẩm trong kho không đủ!");
        }

        Optional<OrderDetail> existingDetailOpt = orderDetailRepository.findByOrderIdAndProductId(orderId, detailDTO.getProductId());

        OrderDetail savedDetail;
        if (existingDetailOpt.isPresent()) {
            // Đã tồn tại -> Cập nhật số lượng
            OrderDetail existingDetail = existingDetailOpt.get();
            int newQuantity = existingDetail.getQuantity() + detailDTO.getQuantity();
            existingDetail.setQuantity(newQuantity); // Cập nhật tạm thời để lấy ID
            // Gọi hàm UPDATE native chỉ cho quantity
            orderDetailRepository.updateOrderDetailQuantityNative(existingDetail.getId(), newQuantity);
            savedDetail = existingDetail; // Trả về đối tượng đã cập nhật (chỉ quantity)
        } else {
            // Chưa có -> Tạo mới
            String newDetailId = generateOrderDetailId();
            double price = product.getPrice(); // Lấy giá từ sản phẩm gốc
            // Gọi hàm INSERT native
            orderDetailRepository.insertOrderDetailNative(
                    newDetailId,
                    orderId,
                    detailDTO.getProductId(),
                    detailDTO.getQuantity(),
                    price
            );
            // Fetch lại để có đối tượng trả về
            savedDetail = orderDetailRepository.findById(newDetailId)
                    .orElseThrow(() -> new RuntimeException("Lỗi khi thêm chi tiết đơn hàng."));
        }

        // Cập nhật lại số lượng tồn kho bằng hàm native
        int newProductQuantity = product.getQuantity() - detailDTO.getQuantity();
        productRepository.updateProductQuantityNative(product.getId(), newProductQuantity);

        orderService.updateOrderTotalAmount(orderId);

        // Cần fetch lại savedDetail để có thông tin đầy đủ sau khi insert/update
        return orderDetailRepository.findById(savedDetail.getId()).orElse(savedDetail);
    }

    // Cập nhật đơn hàng
    @Transactional
    public OrderDetail updateOrderDetail(String detailId, int newQuantity) {
        if (newQuantity <= 0) {
            deleteOrderDetail(detailId);
            return null;
        }

        OrderDetail detail = orderDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đơn hàng"));

        Product product = detail.getProduct();
        int oldQuantity = detail.getQuantity();
        int quantityChange = newQuantity - oldQuantity;

        if (product.getQuantity() < quantityChange) {
            throw new RuntimeException("Số lượng sản phẩm trong kho không đủ để cập nhật!");
        }

        // Gọi hàm UPDATE native cho quantity
        orderDetailRepository.updateOrderDetailQuantityNative(detailId, newQuantity);

        // Cập nhật lại tồn kho bằng hàm native
        int newProductQuantity = product.getQuantity() - quantityChange;
        productRepository.updateProductQuantityNative(product.getId(), newProductQuantity);

        orderService.updateOrderTotalAmount(detail.getOrder().getId());

        // Fetch lại để trả về đối tượng đã cập nhật
        return orderDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Lỗi khi cập nhật chi tiết đơn hàng."));
    }

    // Xoá một chi tiết đơn hàng
    @Transactional
    public void deleteOrderDetail(String detailId) {
        OrderDetail detail = orderDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đơn hàng"));

        Order order = detail.getOrder();
        Product product = detail.getProduct();
        int quantityToReturn = detail.getQuantity();

        // Gọi hàm DELETE native
        orderDetailRepository.deleteOrderDetailById(detailId);

        // Hoàn trả lại số lượng vào kho bằng hàm native
        int newProductQuantity = product.getQuantity() + quantityToReturn;
        productRepository.updateProductQuantityNative(product.getId(), newProductQuantity);

        orderService.updateOrderTotalAmount(order.getId());
    }

    // Tìm chi tiết đơn hàng của một đơn hàng
    public List<OrderDetail> findByOrderId(String orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}