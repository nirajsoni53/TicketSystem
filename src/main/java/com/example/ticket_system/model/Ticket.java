package com.example.ticket_system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private String ticketId; // UUID
    private String subject;
    private String description;
    private TicketStatus status;
    private String userId; // ID of the user who created the ticket
    private String assigneeId; // ID of the agent assigned to the ticket (nullable)
    private Instant createdAt;
    private Instant updatedAt;

    public Ticket(String subject, String description, String userId) {
        this.ticketId = UUID.randomUUID().toString();
        this.subject = subject;
        this.description = description;
        this.status = TicketStatus.OPEN; // Default status
        this.userId = userId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
