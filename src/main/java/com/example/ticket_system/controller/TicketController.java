package com.example.ticket_system.controller;

import com.example.ticket_system.dto.CreateTicketRequest;
import com.example.ticket_system.model.Role;
import com.example.ticket_system.model.Ticket;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.ticket_system.service.TicketService;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody CreateTicketRequest request) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Ticket createdTicket = ticketService.createTicket(request.getSubject(), request.getDescription(), userId);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> listTickets() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getPrincipal();

        // Extract role from authentication authorities
        Role role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .map(auth -> Role.valueOf(auth.substring(5))) // Remove "ROLE_" prefix
                .findFirst()
                    .orElseThrow(() -> new IllegalStateException("User has no role assigned."));

        List<Ticket> tickets = ticketService.listTickets(userId, role);
        return ResponseEntity.ok(tickets);
    }
}
