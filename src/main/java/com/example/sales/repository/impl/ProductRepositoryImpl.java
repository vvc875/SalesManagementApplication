package com.example.sales.repository.impl;
import com.example.sales.entity.Product;
import com.example.sales.repository.ProductRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public class ProductRepositoryImpl implements ProductRepository {
    private final JdbcTemplate jdbcTemplate;
    public ProductRepositoryImpl(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    private final RowMapper<Product> productRowMapper = (rs, rowNum) -> {
        Product product = new Product();
        product.setId(rs.getInt("p_id"));
        product.setName(rs.getString("p_name"));
        product.setDescription(rs.getString("p_description"));
        product.setPrice(rs.getBigDecimal("p_price"));
        product.setQuantity(rs.getInt("p_quantity"));
        product.setCategoryId(rs.getInt("p_category_id"));
        product.setCategoryName(rs.getString("c_name"));
        return product;
    };

    @Override
    public List<Product> findAll() {
        final String sql = "SELECT p.id as p_id, p.name as p_name, p.description as p_description, p.price as p_price, " +
                "p.quantity as p_quantity, p.category_id as p_category_id, c.name as c_name " +
                "FROM products p LEFT JOIN categories c ON p.category_id = c.id";
        return jdbcTemplate.query(sql, productRowMapper);
    }

    @Override
    public Optional<Product> findById(Integer id) {
        final String sql = "SELECT p.id as p_id, p.name as p_name, p.description as p_description, p.price as p_price, " +
                "p.quantity as p_quantity, p.category_id as p_category_id, c.name as c_name " +
                "FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.id = ?";
        try {
            Product product = jdbcTemplate.queryForObject(sql, new Object[]{id}, productRowMapper);
            return Optional.ofNullable(product);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Product save(Product product) {
        final String sql = "INSERT INTO products (name, description, price, quantity, category_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, product.getName(), product.getDescription(), product.getPrice(), product.getQuantity(), product.getCategoryId());
        return product;
    }

    @Override
    public int update(Product product) {
        final String sql = "UPDATE products SET name = ?, description = ?, price = ?, quantity = ?, category_id = ? WHERE id = ?";
        return jdbcTemplate.update(sql, product.getName(), product.getDescription(), product.getPrice(), product.getQuantity(), product.getCategoryId(), product.getId());
    }

    @Override
    public int deleteById(Integer id) {
        final String sql = "DELETE FROM products WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}