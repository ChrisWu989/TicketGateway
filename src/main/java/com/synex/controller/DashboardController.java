package com.synex.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.synex.entity.Ticket;
import com.synex.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
	private final DashboardService dashboardService;
	
	public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/user/{employeeId}")
    public List<Ticket> userDashboard(@PathVariable Long employeeId) {
        return dashboardService.getUserTickets(employeeId);
    }

    @GetMapping("/manager/{employeeId}")
    public List<Ticket> managerDashboard(@PathVariable Long employeeId) {
        return dashboardService.getManagerTickets(employeeId);
    }

    @GetMapping("/admin")
    public List<Ticket> adminDashboard() {
        return dashboardService.getAdminTickets();
    }
}
