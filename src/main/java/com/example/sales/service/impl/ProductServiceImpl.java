package com.example.sales.service.impl;
import com.example.sales.entity.Product;
import com.example.sales.repository.ProductRepository;
import com.example.sales.service.ProductService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Override
    public List<Product> getAllProducts() { return productRepository.findAll(); }
    @Override
    public Optional<Product> getProductById(Integer id) { return productRepository.findById(id); }
    @Override
    public void saveProduct(Product product) {
        if (product.getId() != null && product.getId() > 0) {
            productRepository.update(product);
        } else {
            productRepository.save(product);
        }
    }
    @Override
    public void deleteProductById(Integer id) { productRepository.deleteById(id); }
}