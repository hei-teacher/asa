package school.hei.asa.endpoint.rest.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

/** TEMPORARY diagnostic: log the id_token header (alg, kid, typ) when its validation fails. */
@Component
@Slf4j
public class JwtHeaderLoggingDecoderFactory implements JwtDecoderFactory<ClientRegistration> {

  private final JwtDecoderFactory<ClientRegistration> delegate = new OidcIdTokenDecoderFactory();

  @Override
  public JwtDecoder createDecoder(ClientRegistration clientRegistration) {
    var decoder = delegate.createDecoder(clientRegistration);
    return token -> {
      try {
        return decoder.decode(token);
      } catch (JwtException e) {
        log.error("id_token rejected, header={}", header(token));
        throw e;
      }
    };
  }

  // Only the first segment (header) is decoded: never the payload or the signature.
  private static String header(String token) {
    try {
      return new String(
          Base64.getUrlDecoder().decode(token.substring(0, token.indexOf('.'))),
          StandardCharsets.UTF_8);
    } catch (RuntimeException e) {
      return "unreadable";
    }
  }
}
