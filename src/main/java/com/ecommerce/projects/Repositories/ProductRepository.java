package com.ecommerce.projects.Repositories;

import com.ecommerce.projects.model.Category;
import com.ecommerce.projects.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pagedetails);

    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pagedetails);
}
