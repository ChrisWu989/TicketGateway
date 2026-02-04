package com.synex.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.synex.entity.Ticket;
import com.synex.entity.TicketHistory;
import com.synex.repository.TicketHistoryRepository;
import com.synex.repository.TicketRepository;

@Service
public class TicketHistoryService {

    private final TicketRepository ticketRepository;
    private final TicketHistoryRepository historyRepository;

    public TicketHistoryService(TicketRepository ticketRepository,
                                TicketHistoryRepository historyRepository) {
        this.ticketRepository = ticketRepository;
        this.historyRepository = historyRepository;
    }

    public List<TicketHistory> getHistoryForTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        return historyRepository.findByTicketOrderByActionDateAsc(ticket);
    }
}
