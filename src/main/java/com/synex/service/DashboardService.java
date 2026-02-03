package com.synex.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.synex.entity.Employee;
import com.synex.entity.Ticket;
import com.synex.repository.EmployeeRepository;
import com.synex.repository.TicketRepository;

@Service
public class DashboardService {
	private final TicketRepository ticketRepository;
	private final EmployeeRepository employeeRepository;
	
	public DashboardService(TicketRepository ticketRepository, 
							EmployeeRepository employeeRepository) {
		this.ticketRepository = ticketRepository;
		this.employeeRepository = employeeRepository;
	}
	
	// USER dashboard
    public List<Ticket> getUserTickets(Long employeeId) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return ticketRepository.findByCreatedBy(emp);
    }

    // MANAGER dashboard
    public List<Ticket> getManagerTickets(Long managerId) {
        return ticketRepository.findAll().stream()
                .filter(t -> t.getCreatedBy() != null &&
                             managerId.equals(t.getCreatedBy().getManagerId()))
                .toList();
    }

    // ADMIN dashboard
    public List<Ticket> getAdminTickets() {
        return ticketRepository.findAll();
    }
	
	
}
