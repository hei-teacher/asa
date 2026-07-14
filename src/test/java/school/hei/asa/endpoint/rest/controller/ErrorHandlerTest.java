package school.hei.asa.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

class ErrorHandlerTest {

  private final ErrorHandler errorHandler = new ErrorHandler();

  @Test
  void handleException_returns_error_view_and_adds_message() {
    Model model = new BindingAwareModelMap();
    var exception = new RuntimeException("Test error message");

    var viewName = errorHandler.handleException(exception, model);

    assertEquals("error", viewName);
    assertEquals("Test error message", model.getAttribute("errorMessage"));
  }
}
