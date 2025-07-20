package com.example.ticket_system.repository;

import com.example.ticket_system.model.Ticket;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class TicketRepository {

    private final Map<String, Ticket> tickets = new ConcurrentHashMap<>();

    public Ticket save(Ticket ticket) {
        tickets.put(ticket.getTicketId(), ticket);
        ticket.setUpdatedAt(Instant.now());
        return ticket;
    }

    public Optional<Ticket> findById(String ticketId) {
        return Optional.ofNullable(tickets.get(ticketId));
    }

    public List<Ticket> findByUserId(String userId) {
        return tickets.values().stream()
                .filter(ticket -> ticket.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Ticket> findByAssigneeId(String assigneeId) {
        return tickets.values().stream()
                .filter(ticket -> ticket.getAssigneeId() != null && ticket.getAssigneeId().equals(assigneeId))
                .collect(Collectors.toList());
    }
}
