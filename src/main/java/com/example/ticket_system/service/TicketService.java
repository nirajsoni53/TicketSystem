package com.example.ticket_system.service;

import com.example.ticket_system.model.Role;
import com.example.ticket_system.model.Ticket;
import com.example.ticket_system.model.User;
import com.example.ticket_system.repository.TicketRepository;
import com.example.ticket_system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    public Ticket createTicket(String subject, String description, String userId) {
        Ticket ticket = new Ticket(subject, description, userId);
        List<User> agents = userRepository.findByRole(Role.AGENT);
        if (!agents.isEmpty()) {
            ticket.setAssigneeId(agents.get(random.nextInt(agents.size())).getUserId());
        }
        return ticketRepository.save(ticket);
    }

    public List<Ticket> listTickets(String currentUserId, Role currentUserRole) {
        if (currentUserRole == Role.USER) {
            // Users can only view their own tickets
            return ticketRepository.findByUserId(currentUserId);
        } else if (currentUserRole == Role.AGENT) {
            // Agents can only view tickets assigned to them
            return ticketRepository.findByAssigneeId(currentUserId);
        }
        return List.of();
    }
}
