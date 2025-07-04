package com.ecommerce.projects.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long productId;

    @NotBlank
    @Size(min =3 , message= " Product name must contain at least 3 character")
    private String productName;
    private String image;

    @NotBlank
    @Size(min =6 , message= " Product name must contain at least 6 character")
    private String description;
    private Integer quantity;
    private double price; //100
    private double discount; //25%
    private double specialPrice;

    @ManyToOne
    @JoinColumn(name="categpry_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name="seller_id")
    private User user;

}
