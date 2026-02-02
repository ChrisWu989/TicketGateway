package com.synex.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
            TicketRepository ticketRepo
    ) {
        return args -> {

            // Roles
            Role userRole = roleRepo.save(new Role(null, RoleName.USER));
            Role adminRole = roleRepo.save(new Role(null, RoleName.ADMIN));

            // Employees
            Employee emp = new Employee();
            emp.setName("Alice");
            emp.setEmail("alice@test.com");
            emp.setPassword("encrypted");
            emp.setDepartment("IT");
            emp.setRoles(List.of(userRole));
            employeeRepo.save(emp);

            Employee admin = new Employee();
            admin.setName("Bob");
            admin.setEmail("bob@test.com");
            admin.setPassword("encrypted");
            admin.setRoles(List.of(adminRole));
            employeeRepo.save(admin);

            // Ticket
            Ticket ticket = new Ticket();
            ticket.setTitle("VPN not working");
            ticket.setDescription("Cannot connect to VPN");
            ticket.setCreatedBy(emp);
            ticket.setAssignee(admin);
            ticket.setPriority(TicketPriority.HIGH);
            ticket.setStatus(TicketStatus.OPEN);
            ticket.setCreationDate(new Date());
            ticket.setCategory("Network");

            ticketRepo.save(ticket);

            System.out.println("✅ H2 test data loaded");
        };
    }
}
