package com.synex.entity;

import java.util.Date;

import com.synex.enums.TicketAction;

import jakarta.persistence.*;

@Entity
@Table(name = "ticket_history")
public class TicketHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    private TicketAction action;
    // CREATED, APPROVED, REJECTED, ASSIGNED, RESOLVED, CLOSED, REOPENED

    @ManyToOne
    @JoinColumn(name = "action_by")
    private Employee actionBy;

    private Date actionDate;

    private String comments;
}
