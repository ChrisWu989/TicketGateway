package com.synex.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.synex.entity.*;
import com.synex.enums.*;
import com.synex.repository.*;

import java.util.List;
import java.util.Date;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(
            EmployeeRepository employeeRepo,
            RoleRepository roleRepo,
            TicketRepository ticketRepo,
           PasswordEncoder encoder
    ) {
        return args -> {

            // Roles
            Role userRole = roleRepo.save(new Role(null, RoleName.USER));
            Role managerRole = roleRepo.save(new Role(null, RoleName.MANAGER));
            Role adminRole = roleRepo.save(new Role(null, RoleName.ADMIN));

            // Manager
            Employee manager = new Employee();
            manager.setName("Jeff");
            manager.setEmail("jeff@test.com");
            manager.setPassword( encoder.encode("password123"));
            manager.setDepartment("Sales");
            manager.setRoles(List.of(managerRole));
            manager = employeeRepo.save(manager); // SAVE FIRST

            // User (employee)
            Employee emp = new Employee();
            emp.setName("Alice");
            emp.setEmail("alice@test.com");
            emp.setPassword(encoder.encode("password123"));
            emp.setDepartment("IT");
            emp.setRoles(List.of(userRole));
            emp.setManagerId(manager.getId());
            emp = employeeRepo.save(emp);

            // Admin
            Employee admin = new Employee();
            admin.setName("Bob");
            admin.setEmail("bob@test.com");
            admin.setPassword(encoder.encode("password123"));
            admin.setRoles(List.of(adminRole));
            admin = employeeRepo.save(admin);

            // Ticket
//            Ticket ticket = new Ticket();
//            ticket.setTitle("VPN not working");
//            ticket.setDescription("Cannot connect to VPN");
//            ticket.setCreatedBy(emp);
//            ticket.setAssignee(admin);
//            ticket.setPriority(TicketPriority.HIGH);
//            ticket.setStatus(TicketStatus.OPEN);
//            ticket.setCreationDate(new Date());
//            ticket.setCategory("Network");
//
//            ticketRepo.save(ticket);

            System.out.println("✅ H2 test data loaded");
        };
    }
}