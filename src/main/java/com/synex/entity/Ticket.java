package com.synex.entity;

import java.util.Date;
import java.util.List;

import com.synex.enums.TicketPriority;
import com.synex.enums.TicketStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "tickets")
public class Ticket {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Employee createdBy;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private Employee assignee;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority; // LOW, MEDIUM, HIGH

    @Enumerated(EnumType.STRING)
    private TicketStatus status;
    // OPEN, PENDING_APPROVAL, APPROVED, REJECTED, ASSIGNED, RESOLVED, CLOSED, REOPENED

    private Date creationDate;

    private String category;

    private String fileAttachmentPath;
    
    private String originalFileName;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<TicketHistory> history;
    
	public Ticket() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Ticket(Long id, String title, String description, Employee createdBy, Employee assignee,
			TicketPriority priority, TicketStatus status, Date creationDate, String category, String fileAttachmentPath,
			String originalFileName, List<TicketHistory> history) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.createdBy = createdBy;
		this.assignee = assignee;
		this.priority = priority;
		this.status = status;
		this.creationDate = creationDate;
		this.category = category;
		this.fileAttachmentPath = fileAttachmentPath;
		this.originalFileName = originalFileName;
		this.history = history;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Employee getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Employee createdBy) {
		this.createdBy = createdBy;
	}

	public Employee getAssignee() {
		return assignee;
	}

	public void setAssignee(Employee assignee) {
		this.assignee = assignee;
	}

	public TicketPriority getPriority() {
		return priority;
	}

	public void setPriority(TicketPriority priority) {
		this.priority = priority;
	}

	public TicketStatus getStatus() {
		return status;
	}

	public void setStatus(TicketStatus status) {
		this.status = status;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getFileAttachmentPath() {
		return fileAttachmentPath;
	}

	public void setFileAttachmentPath(String fileAttachmentPath) {
		this.fileAttachmentPath = fileAttachmentPath;
	}
	
	public String getOriginalFileName() {
	    return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
	    this.originalFileName = originalFileName;
	}
	
	public List<TicketHistory> getHistory() {
		return history;
	}

	public void setHistory(List<TicketHistory> history) {
		this.history = history;
	}
    
}
