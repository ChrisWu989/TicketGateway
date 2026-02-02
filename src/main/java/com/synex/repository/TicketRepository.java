package com.synex.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synex.entity.Ticket;
import com.synex.entity.Employee;
import com.synex.enums.TicketPriority;
import com.synex.enums.TicketStatus;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
