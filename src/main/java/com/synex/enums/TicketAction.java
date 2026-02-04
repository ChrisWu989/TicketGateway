package com.synex.enums;

public enum TicketAction {
    CREATED,		// Created by User
    APPROVED,		// Approved by Manager
    REJECTED,		// Rejected by Manager
    ASSIGNED,		// Assigned to Admin
    RESOLVED,		// Resolved by Admin
    CLOSED,			// Closed by Admin after resolution
    REOPENED		// Reopened by user after closed 
}