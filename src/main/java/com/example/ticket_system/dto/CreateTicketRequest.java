package com.example.ticket_system.dto;

import lombok.Data;

@Data
public class CreateTicketRequest {
    private String subject;
    private String description;
}
