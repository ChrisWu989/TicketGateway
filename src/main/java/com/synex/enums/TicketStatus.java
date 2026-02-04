package com.synex.enums;

public enum TicketStatus {
    OPEN,				// Created by User
    PENDING_APPROVAL,	// Wait for manager after creations
    APPROVED,			// Manager approves 
    REJECTED,			// Manager rejects
    ASSIGNED,			// Admin assigned ticket
    RESOLVED,			// Admin resolves ticket
    CLOSED,				// Admin closes after resolution
    REOPENED			// User reopens from closed ticket
}