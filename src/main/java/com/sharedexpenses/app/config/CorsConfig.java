package com.sharedexpenses.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir qualquer origem
        configuration.addAllowedOriginPattern("*");
        
        // Permitir todos os métodos HTTP
        configuration.addAllowedMethod("*");
        
        // Permitir todos os headers
        configuration.addAllowedHeader("*");
        
        // Permitir credenciais (necessário para JWT)
        configuration.setAllowCredentials(true);
        
        // Expor headers de resposta para o frontend
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Content-Type");
        
        // Configurar cache para preflight requests (1 hora)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
