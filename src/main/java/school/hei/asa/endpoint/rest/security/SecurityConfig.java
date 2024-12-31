package school.hei.asa.endpoint.rest.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final String cognitoClientId;
  private final String cognitoLogoutUrl;
  private final String asaLogoutUrl;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;

  public SecurityConfig(
      @Value("${spring.security.oauth2.client.registration.cognito.clientid}")
          String cognitoClientId,
      @Value("${cognito.logout.url}") String cognitoLogoutUrl,
      @Value("${asa.logout.url}") String asaLogoutUrl,
      OAuth2SuccessHandler oAuth2SuccessHandler) {
    this.cognitoClientId = cognitoClientId;
    this.cognitoLogoutUrl = cognitoLogoutUrl;
    this.asaLogoutUrl = asaLogoutUrl;
    this.oAuth2SuccessHandler = oAuth2SuccessHandler;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(Customizer.withDefaults())
        .authorizeHttpRequests(
            authz -> authz.requestMatchers("/").permitAll().anyRequest().authenticated())
        .oauth2Login(oauth2 -> oauth2.successHandler(oAuth2SuccessHandler))
        .logout(
            logout ->
                logout.logoutSuccessHandler(
                    (request, response, authentication) ->
                        response.sendRedirect(
                            cognitoLogoutUrl
                                + "?client_id="
                                + cognitoClientId
                                + "&logout_uri="
                                + asaLogoutUrl)));
    return http.build();
  }
}
