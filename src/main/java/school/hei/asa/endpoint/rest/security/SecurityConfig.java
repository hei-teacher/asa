package school.hei.asa.endpoint.rest.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  public SecurityConfig() {
    // Constructor removed as OAuth2/Cognito related dependencies are removed
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(Customizer.withDefaults())
        .authorizeHttpRequests(
            authz -> authz.requestMatchers("/").permitAll().anyRequest().authenticated())
        .formLogin(Customizer.withDefaults()) // Added basic form login
        .logout(logout -> logout.logoutSuccessUrl("/")); // Added basic logout
    return http.build();
  }
}
