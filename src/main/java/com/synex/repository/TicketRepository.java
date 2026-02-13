package com.synex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synex.entity.Ticket;
import com.synex.entity.Employee;
import com.synex.enums.TicketStatus;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCreatedBy(Employee employee);

    List<Ticket> findByAssignee(Employee assignee);

    List<Ticket> findByStatus(TicketStatus status);
    
    List<Ticket> findByCreatedByOrderByCreationDateDesc(Employee createdBy);
    
    List<Ticket> findByStatusOrderByCreationDateAsc(TicketStatus status);
    
    List<Ticket> findByCreatedBy_ManagerIdOrderByCreationDateDesc(Long managerId);
}
