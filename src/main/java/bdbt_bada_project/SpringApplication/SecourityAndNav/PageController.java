package bdbt_bada_project.SpringApplication.SecourityAndNav;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/main_student")
    public String studentPage() {
        return "student/main_student";
    }

    @GetMapping("/main_lecturer")
    public String lecturerPage() {
        return "lecturer/main_lecturer";
    }

    @GetMapping("/main_admin")
    public String adminPage() {
        return "admin/main_admin";
    }
}
