package com.ecommerce.projects.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;


@Entity(name="Categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

   @NotBlank
   @Size(min = 4,message = "Category name must be 4 character")
    private String categoryName;

   @OneToMany(mappedBy="category",cascade = CascadeType.ALL)
   private List<Product> products;

}
