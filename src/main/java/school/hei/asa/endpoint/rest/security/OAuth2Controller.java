package school.hei.asa.endpoint.rest.security;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

  @Value("${spring.security.oauth2.client.registration.cognito.client-id}")
  private String clientId;

  @Value("${spring.security.oauth2.client.registration.cognito.client-secret}")
  private String clientSecret;

  @Value("${spring.security.oauth2.client.registration.cognito.redirect-uri}")
  private String redirectUri;

  @Value("${spring.security.oauth2.client.provider.cognito.token-uri}")
  private String tokenUri;

  @Value("${spring.security.oauth2.client.provider.cognito.authorization-uri}")
  private String authorizationUri;

  @Value("${spring.security.oauth2.client.registration.cognito.scope}")
  private String scope;

  private final RestTemplate restTemplate = new RestTemplate();

  // Step 1: Redirect to Cognito login
  @GetMapping("/authorize")
  public void redirectToCognito(HttpServletResponse response) throws IOException {
    String state = UUID.randomUUID().toString(); // Optional: add CSRF or other tracking

    String redirectUrl =
        UriComponentsBuilder.fromHttpUrl(authorizationUri)
            .queryParam("response_type", "code")
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("scope", scope)
            .queryParam("state", state)
            .build()
            .toUriString();

    response.sendRedirect(redirectUrl);
  }

  // Step 2: Callback after Cognito login
  @GetMapping("/callback")
  public ResponseEntity<?> callback(
      @RequestParam String code, @RequestParam(required = false) String state) {
    try {
      return ResponseEntity.ok(exchangeCodeForTokens(code));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Failed to exchange token: " + e.getMessage());
    }
  }

  // Step 3: Token exchange using RestTemplate
  private Map<String, Object> exchangeCodeForTokens(String code) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    // Basic Auth with client ID and secret
    String auth = clientId + ":" + clientSecret;
    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    headers.set("Authorization", "Basic " + encodedAuth);

    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "authorization_code");
    form.add("code", code);
    form.add("redirect_uri", redirectUri);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

    ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);

    return response.getBody();
  }
}
