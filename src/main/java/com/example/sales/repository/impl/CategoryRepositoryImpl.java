package com.example.sales.repository.impl;
import com.example.sales.entity.Category;
import com.example.sales.repository.CategoryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public class CategoryRepositoryImpl implements CategoryRepository {
    private final JdbcTemplate jdbcTemplate;
    public CategoryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private final RowMapper<Category> rowMapper = (rs, rowNum) -> {
        Category category = new Category();
        category.setId(rs.getInt("id"));
        category.setName(rs.getString("name"));
        return category;
    };
    @Override
    public List<Category> findAll() {
        return jdbcTemplate.query("SELECT * FROM categories", rowMapper);
    }
    @Override
    public Optional<Category> findById(Integer id) {
        try {
            Category cat = jdbcTemplate.queryForObject("SELECT * FROM categories WHERE id = ?", new Object[]{id}, rowMapper);
            return Optional.ofNullable(cat);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}