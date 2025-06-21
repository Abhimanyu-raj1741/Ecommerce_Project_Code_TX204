package com.ecommerce.projects.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @Size(min = 5, message="Street name must be atleast 5 Character")
    @NotBlank
    private String street;

    @NotBlank
    @Size(min = 5, message="Building name must be atleast 5 Character")
    private String buildingName;

    @NotBlank
    @Size(min = 5, message="City name must be atleast 5 Character")
    private String city;

    @NotBlank
    @Size(min = 2, message="State name must be atleast 2 Character")
    private String state;


    @NotBlank
    @Size(min = 6, message="Pincode must be atleast 6 Character")
    private String Pincode;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users = new ArrayList<>();

    public Address(Long addressId, String street, String buildingName, String city, String state, String pincode) {
        this.addressId = addressId;
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.state = state;
        Pincode = pincode;
    }
}
