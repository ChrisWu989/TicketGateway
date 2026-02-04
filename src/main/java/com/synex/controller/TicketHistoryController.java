package com.synex.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.synex.entity.TicketHistory;
import com.synex.service.TicketHistoryService;

@RestController
@RequestMapping("/api/tickets")
public class TicketHistoryController {

    private final TicketHistoryService historyService;

    public TicketHistoryController(TicketHistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/{ticketId}/history")
    public List<TicketHistory> getTicketHistory(@PathVariable Long ticketId) {
        return historyService.getHistoryByTicketId(ticketId);
    }
}