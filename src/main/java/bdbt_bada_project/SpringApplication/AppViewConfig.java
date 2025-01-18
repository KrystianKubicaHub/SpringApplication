package bdbt_bada_project.SpringApplication;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppViewConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Główne widoki (publiczne)
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("login");

        // Widoki dla użytkowników z rolą USER
        registry.addViewController("/user/main_user").setViewName("user/main_user");
        registry.addViewController("/user/experiment").setViewName("user/experiment");


        // Widoki dla użytkowników z rolą ADMIN
        registry.addViewController("/admin/main_admin").setViewName("admin/main_admin");

        // Widoki dla błędów
        registry.addViewController("/errors/403").setViewName("errors/403");
        registry.addViewController("/errors/404").setViewName("errors/404");
        registry.addViewController("/errors/500").setViewName("errors/500");
        registry.addViewController("/errors/504").setViewName("errors/504");
        registry.addViewController("/errors/other").setViewName("errors/other");
    }
}
