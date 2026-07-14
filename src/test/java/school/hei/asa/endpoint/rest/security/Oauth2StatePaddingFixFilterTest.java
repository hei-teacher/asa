package school.hei.asa.endpoint.rest.security;

import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class Oauth2StatePaddingFixFilterTest {

  private final Oauth2StatePaddingFixFilter filter = new Oauth2StatePaddingFixFilter();

  @Test
  void doFilter_without_state_does_not_modify() throws Exception {
    var request = new MockHttpServletRequest();
    request.setRequestURI("/login/oauth2/code/casdoor");
    var response = new MockHttpServletResponse();
    var chain = mock(FilterChain.class);

    filter.doFilterInternal(request, response, chain);

    verify(chain).doFilter(request, response);
  }

  @Test
  void doFilter_with_state_needing_padding_fixes_it() throws Exception {
    var request = new MockHttpServletRequest();
    request.setRequestURI("/login/oauth2/code/casdoor");
    request.addParameter("state", "abc");
    var response = new MockHttpServletResponse();
    var chain = mock(FilterChain.class);

    filter.doFilterInternal(request, response, chain);

    verify(chain).doFilter(any(HttpServletRequest.class), eq(response));
  }

  @Test
  void doFilter_on_non_oauth_path_does_not_modify() throws Exception {
    var request = new MockHttpServletRequest();
    request.setRequestURI("/some-other-path");
    request.addParameter("state", "abc");
    var response = new MockHttpServletResponse();
    var chain = mock(FilterChain.class);

    filter.doFilterInternal(request, response, chain);

    verify(chain).doFilter(request, response);
  }

  @Test
  void doFilter_with_state_already_padded_does_not_wrap() throws Exception {
    var request = new MockHttpServletRequest();
    request.setRequestURI("/login/oauth2/code/casdoor");
    request.addParameter("state", "abcd");
    var response = new MockHttpServletResponse();
    var chain = mock(FilterChain.class);

    filter.doFilterInternal(request, response, chain);

    verify(chain).doFilter(request, response);
  }

  @Test
  void doFilter_with_null_state_on_oauth_path_does_not_pad() throws Exception {
    var request = new MockHttpServletRequest();
    request.setRequestURI("/login/oauth2/code/casdoor");
    request.addParameter("state", (String) null);
    var response = new MockHttpServletResponse();
    var chain = mock(FilterChain.class);

    filter.doFilterInternal(request, response, chain);

    verify(chain).doFilter(request, response);
  }
}
