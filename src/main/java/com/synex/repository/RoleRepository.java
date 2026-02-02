package com.synex.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synex.entity.Role;
import com.synex.enums.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(RoleName name);
}
