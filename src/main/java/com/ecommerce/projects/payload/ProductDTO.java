package com.ecommerce.projects.payload;


import com.ecommerce.projects.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long productId;
    private String productName;
    private String image;
    private String description;
    private Integer quantity;
    private double price; //100
    private double discount; //25%
    private double specialPrice;


}
