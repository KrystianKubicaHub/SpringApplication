package bdbt_bada_project.SpringApplication;
import bdbt_bada_project.SpringApplication.DataModels.StudentData;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {

    // Obsługa GET i POST dla "/main"
    @RequestMapping(value = "/main", method = {RequestMethod.GET, RequestMethod.POST})
    public String handleMain(HttpServletRequest request) {
        if (request.isUserInRole("ADMIN")) {
            return "redirect:/admin/main_admin";
        } else if (request.isUserInRole("USER")) {
            return "redirect:/user/main_user";
        } else {
            return "redirect:/index";
        }
    }

    // Obsługa GET dla "/user/main_user" z ModelAndView
    @RequestMapping(value = "/user/main_user", method = RequestMethod.GET)
    public ModelAndView handleUserMain() {
        // Pobieranie danych studenta
        StudentData student = StudentData.getInstance();

        // Tworzenie obiektu ModelAndView
        ModelAndView modelAndView = new ModelAndView("user/main_user");
        modelAndView.addObject("student", student);

        // Zwracanie widoku z danymi
        return modelAndView;
    }

    // Obsługa GET i POST dla "/logout"
    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String logoutHandler() {
        return "redirect:/index";
    }
}
