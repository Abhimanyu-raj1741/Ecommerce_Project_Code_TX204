package com.ecommerce.projects.Repositories;

import com.ecommerce.projects.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {


    Optional<User> findByUserName(String username);

    boolean existsByUserName( String username);

    boolean existsByEmail( String email);
}
