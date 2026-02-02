package com.synex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synex.entity.Ticket;
import com.synex.entity.TicketHistory;

public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {

    List<TicketHistory> findByTicketOrderByActionDateAsc(Ticket ticket);
}