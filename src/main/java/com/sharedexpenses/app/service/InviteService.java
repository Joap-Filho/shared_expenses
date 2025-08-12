package com.sharedexpenses.app.service;

import com.sharedexpenses.app.entity.ExpenseSpaceInvite;
import com.sharedexpenses.app.repository.ExpenseSpaceInviteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class InviteService {

    @Autowired
    private ExpenseSpaceInviteRepository inviteRepository;

    @Autowired
    private EmailService emailService;

    public ExpenseSpaceInvite createInvite(Long expenseSpaceId, Long createdByUserId, String inviteeEmail, String inviteeName) {
        ExpenseSpaceInvite invite = new ExpenseSpaceInvite();
        invite.setExpenseSpaceId(expenseSpaceId);
        invite.setCreatedByUserId(createdByUserId);

        String token = UUID.randomUUID().toString().replace("-", "");
        invite.setToken(token);

        LocalDateTime now = LocalDateTime.now();
        invite.setCreatedAt(now);
        invite.setExpirationDate(now.plusHours(1)); // expira em 1h
        invite.setUsed(false);

        inviteRepository.save(invite);

        emailService.sendInviteEmail(inviteeEmail, inviteeName, token);

        return invite;
    }

    public ExpenseSpaceInvite validateInvite(String token) {
        ExpenseSpaceInvite invite = inviteRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid invite token"));

        if (invite.isUsed()) {
            throw new RuntimeException("Invite token already used");
        }

        if (invite.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invite token expired");
        }

        return invite;
    }

    public void markInviteUsed(ExpenseSpaceInvite invite) {
        invite.setUsed(true);
        inviteRepository.save(invite);
    }
}

