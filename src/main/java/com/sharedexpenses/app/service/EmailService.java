package com.sharedexpenses.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    private static final String BREVO_SEND_API = "https://api.brevo.com/v3/smtp/email";

    public void sendInviteEmail(String toEmail, String toName, String inviteToken) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> body = new HashMap<>();
        Map<String, String> sender = new HashMap<>();
        sender.put("name", senderName);
        sender.put("email", senderEmail);

        Map<String, String> to = new HashMap<>();
        to.put("email", toEmail);
        to.put("name", toName);

        body.put("sender", sender);
        body.put("to", Collections.singletonList(to));
        body.put("subject", "Convite para grupo Shared Expenses");
        body.put("htmlContent", "<p>Você foi convidado para um grupo no Shared Expenses.</p>"
            + "<p>Clique no link para aceitar o convite:</p>"
            + "<a href=\"https://seusite.com/accept-invite?token=" + inviteToken + "\">Aceitar Convite</a>");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(BREVO_SEND_API, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to send email: " + response.getBody());
        }
    }
}
