package com.synex.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.synex.enums.TicketAction;

import jakarta.persistence.*;

@Entity
@Table(name = "ticket_history")
public class TicketHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
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

    
	public TicketHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TicketHistory(Long id, Ticket ticket, TicketAction action, Employee actionBy, Date actionDate,
			String comments) {
		super();
		this.id = id;
		this.ticket = ticket;
		this.action = action;
		this.actionBy = actionBy;
		this.actionDate = actionDate;
		this.comments = comments;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

	public TicketAction getAction() {
		return action;
	}

	public void setAction(TicketAction action) {
		this.action = action;
	}

	public Employee getActionBy() {
		return actionBy;
	}

	public void setActionBy(Employee actionBy) {
		this.actionBy = actionBy;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
    
    
}
