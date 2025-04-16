package com.ecommerce.projects.Repositories;

import com.ecommerce.projects.model.Category;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface CategoryRepository extends JpaRepository<Category,Long> {


    Category findByCategoryName(@NotBlank @Size(min = 4,message = "Category name must be 4 character") String categoryName);
}
