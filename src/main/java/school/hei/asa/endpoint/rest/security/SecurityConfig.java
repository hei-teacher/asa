package school.hei.asa.endpoint.rest.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

  private final String casdoorClientId;
  private final String casdoorLogoutUrl;
  private final String asaLogoutUrl;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;

  public SecurityConfig(
      @Value("${spring.security.oauth2.client.registration.casdoor.clientid}")
          String casdoorClientId,
      @Value("${casdoor.logout.url}") String casdoorLogoutUrl,
      @Value("${asa.logout.url}") String asaLogoutUrl,
      OAuth2SuccessHandler oAuth2SuccessHandler) {

    this.casdoorClientId = casdoorClientId;
    this.casdoorLogoutUrl = casdoorLogoutUrl;
    this.asaLogoutUrl = asaLogoutUrl;
    this.oAuth2SuccessHandler = oAuth2SuccessHandler;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(
            csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers("/casdoor-logout", "/logout", "/health", "/error"))
        .authorizeHttpRequests(
            authz ->
                authz
                    .requestMatchers(
                        "/",
                        "/health",
                        "/error",
                        "/static/**",
                        "/webjars/**",
                        "/login",
                        "/casdoor-logout",
                        "/oauth2-redirect-fix")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2Login(
            oauth2 ->
                oauth2
                    .successHandler(oAuth2SuccessHandler)
                    .failureHandler(
                        new SimpleUrlAuthenticationFailureHandler("/oauth2/authorization/casdoor")))
        .logout(
            logout ->
                logout.logoutSuccessHandler(
                    (request, response, authentication) -> {
                      var principal = (DefaultOidcUser) authentication.getPrincipal();
                      String accessToken = principal.getIdToken().getTokenValue();
                      log.info("🔒 Logout SUCCESS for user {}", principal.getEmail());
                      response.sendRedirect(
                          "/casdoor-logout?id_token_hint="
                              + accessToken
                              + "&post_logout_redirect_uri="
                              + asaLogoutUrl
                              + "&logout_uri="
                              + casdoorLogoutUrl);
                    }));

    return http.build();
  }
}
