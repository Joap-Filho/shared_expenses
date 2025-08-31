package com.sharedexpenses.app.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtil {
    
    /**
     * Extrai o email do usuário autenticado do contexto de segurança
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Usuário não autenticado");
        }
        return authentication.getName();
    }
    
    /**
     * Verifica se há um usuário autenticado
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getName());
    }
}
