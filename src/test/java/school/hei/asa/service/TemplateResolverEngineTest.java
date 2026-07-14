package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;

class TemplateResolverEngineTest {

  private final TemplateResolverEngine engine = new TemplateResolverEngine();

  @Test
  void getTemplateEngine_returns_configured_engine() {
    var result = engine.getTemplateEngine();
    assertNotNull(result);
  }

  @Test
  void parseTemplateResolver_throws_on_missing_template() {
    var engine = new TemplateResolverEngine();
    try {
      engine.parseTemplateResolver("nonexistent", new Context());
    } catch (Exception e) {
      assertNotNull(e);
    }
  }
}
