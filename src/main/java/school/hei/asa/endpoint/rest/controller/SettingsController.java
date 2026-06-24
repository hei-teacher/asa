package school.hei.asa.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import school.hei.asa.service.AppSettingsService;

@Controller
@AllArgsConstructor
public class SettingsController {

  private final AppSettingsService appSettingsService;

  @GetMapping("/settings")
  public String getSettings(Model model) {
    model.addAttribute(
        "lowContractDaysThreshold", appSettingsService.getLowContractDaysThreshold());
    return "settings";
  }

  @PostMapping("/settings")
  public String updateSettings(
      @RequestParam int lowContractDaysThreshold, RedirectAttributes redirectAttributes) {
    if (lowContractDaysThreshold < 0) {
      redirectAttributes.addFlashAttribute("toastType", "error");
      redirectAttributes.addFlashAttribute(
          "toastMessage", "Le seuil doit etre un nombre positif.");
      return "redirect:/settings";
    }
    appSettingsService.updateLowContractDaysThreshold(lowContractDaysThreshold);
    redirectAttributes.addFlashAttribute("toastType", "success");
    redirectAttributes.addFlashAttribute("toastMessage", "Seuil mis a jour.");
    return "redirect:/settings";
  }
}
