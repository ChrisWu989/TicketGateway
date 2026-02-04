package com.synex.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.synex.entity.Role;
import com.synex.enums.RoleName;
import com.synex.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new RuntimeException("Role not found: " + roleName)
                );
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Role not found with id: " + id)
                );
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}