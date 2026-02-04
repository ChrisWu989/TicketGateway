package com.synex.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.synex.entity.Role;
import com.synex.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<Role> getRoles() {
        return roleService.getAllRoles();
    }
}