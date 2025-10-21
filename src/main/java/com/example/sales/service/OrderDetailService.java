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

@Service
public class OrderDetailService {

    @Autowired private OrderDetailRepository orderDetailRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private OrderService orderService;
    // ID tu dong
    private String generateOrderDetailId() {
        long count = orderDetailRepository.count();
        return String.format("OD%03d", count + 1);
    }

    //Thêm sản phảm vào đơn hàng
    @Transactional
    public OrderDetail addProductToOrder(String orderId, OrderDetailRequestDTO detailDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));
        Product product = productRepository.findById(detailDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));

        if (product.getQuantity() < detailDTO.getQuantity()) {
            throw new RuntimeException("Số lượng sản phẩm trong kho không đủ!");
        }

        // Kiểm tra xem sản phẩm đã có trong đơn hàng chưa
        OrderDetail detail = orderDetailRepository.findByOrderIdAndProductId(orderId, detailDTO.getProductId())
                .map(existingDetail -> {
                    // Nếu đã có -> Cập nhật số lượng
                    existingDetail.setQuantity(existingDetail.getQuantity() + detailDTO.getQuantity());
                    return existingDetail;
                })
                .orElseGet(() -> {
                    // Nếu chưa có -> Tạo mới
                    OrderDetail newDetail = new OrderDetail();
                    newDetail.setId(generateOrderDetailId());
                    newDetail.setOrder(order);
                    newDetail.setProduct(product);
                    newDetail.setQuantity(detailDTO.getQuantity());
                    newDetail.setPrice(product.getPrice()); // Lấy giá từ sản phẩm gốc
                    return newDetail;
                });

        // Cập nhật lại số lượng tồn kho
        product.setQuantity(product.getQuantity() - detailDTO.getQuantity());
        productRepository.save(product);

        OrderDetail savedDetail = orderDetailRepository.save(detail);

        //Tính tiền tổng đơn hàng
        orderService.updateOrderTotalAmount(orderId);

        return savedDetail;
    }

    //Cập nhật số lượng sản phẩm
    @Transactional
    public OrderDetail updateOrderDetail(String detailId, int newQuantity) {
        OrderDetail detail = orderDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đơn hàng"));

        Product product = detail.getProduct();
        int oldQuantity = detail.getQuantity();
        int quantityChange = newQuantity - oldQuantity;

        // Kiểm tra tồn kho
        if (product.getQuantity() < quantityChange) {
            throw new RuntimeException("Số lượng sản phẩm trong kho không đủ để cập nhật!");
        }

        detail.setQuantity(newQuantity);

        // Cập nhật lại tồn kho
        product.setQuantity(product.getQuantity() - quantityChange);
        productRepository.save(product);

        OrderDetail updatedDetail = orderDetailRepository.save(detail);

        // Cập nhật lại tổng tiền đơn hàng
        orderService.updateOrderTotalAmount(detail.getOrder().getId());

        return updatedDetail;
    }

    //Xoá sản phẩm khỏi đơn hàng
    @Transactional
    public void deleteOrderDetail(String detailId) {
        OrderDetail detail = orderDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đơn hàng"));

        Order order = detail.getOrder();
        Product product = detail.getProduct();
        int quantityToReturn = detail.getQuantity();

        // Xóa chi tiết
        orderDetailRepository.delete(detail);

        // Hoàn trả lại số lượng vào kho
        product.setQuantity(product.getQuantity() + quantityToReturn);
        productRepository.save(product);

        // Cập nhật lại tổng tiền đơn hàng
        orderService.updateOrderTotalAmount(order.getId());
    }

    public List<OrderDetail> findByOrderId(String orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}