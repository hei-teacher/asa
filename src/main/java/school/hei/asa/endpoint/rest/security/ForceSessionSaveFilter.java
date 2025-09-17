package school.hei.asa.endpoint.rest.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ForceSessionSaveFilter extends OncePerRequestFilter {

  private final SessionRepository<? extends Session> sessionRepository;

  public ForceSessionSaveFilter(SessionRepository<? extends Session> sessionRepository) {
    this.sessionRepository = sessionRepository;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    filterChain.doFilter(request, response);

    if (request.getSession(false) != null) {
      Session session = sessionRepository.findById(request.getSession().getId());
      if (session != null) {
        ((SessionRepository<Session>) sessionRepository).save((Session) session);
      }
    }
  }
}
