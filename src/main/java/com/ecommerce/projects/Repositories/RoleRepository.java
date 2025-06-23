package com.ecommerce.projects.Repositories;

import com.ecommerce.projects.model.AppRole;
import com.ecommerce.projects.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {



    Optional<Role> findByRoleName(AppRole appRole);
}
