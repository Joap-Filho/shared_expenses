package com.sharedexpenses.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sharedexpenses.app.entity.ExpenseSpace;
import com.sharedexpenses.app.entity.User;
import com.sharedexpenses.app.repository.UserRepository;
import com.sharedexpenses.app.service.ExpenseSpaceService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@RestController
@RequestMapping("/api/expense-spaces")
public class ExpenseSpaceController {

    @Autowired
    private ExpenseSpaceService expenseSpaceService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createExpenseSpace(@RequestParam String name) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        ExpenseSpace expenseSpace = expenseSpaceService.createExpenseSpace(name, user);

        return ResponseEntity.ok(expenseSpace);
    }
}
