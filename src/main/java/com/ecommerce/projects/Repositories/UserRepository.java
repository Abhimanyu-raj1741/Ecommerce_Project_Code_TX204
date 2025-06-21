package com.ecommerce.projects.Repositories;

import com.ecommerce.projects.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {


    Optional<User> findByUserName(String username);
}
