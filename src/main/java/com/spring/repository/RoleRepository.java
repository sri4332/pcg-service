package com.spring.repository;

import java.util.Optional;

import com.spring.models.UserRoles;
import com.spring.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(UserRoles name);
}
