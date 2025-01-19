package bdbt_bada_project.SpringApplication.entities.SecourityAndNav;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppViewConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/student/main_student").setViewName("main_student");
        registry.addViewController("/lecturer/main_lecturer").setViewName("lecturer/main_lecturer");
        registry.addViewController("/admin/main_admin").setViewName("admin/main_admin");
    }
}


