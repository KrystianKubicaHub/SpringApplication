package bdbt_bada_project.SpringApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("user"))
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Wyłączenie CSRF na czas testów
                .authorizeHttpRequests()
                .requestMatchers("/", "/index", "/login", "/errors/**").permitAll() // Publiczne strony
                .requestMatchers("/resources/static/**").permitAll() // Zezwolenie na dostęp do zasobów statycznych
                .requestMatchers("/main").authenticated() // Wymagane uwierzytelnienie dla strony głównej
                .requestMatchers("/admin/**").hasRole("ADMIN") // Ścieżki dla administratora
                .requestMatchers("/user/**").hasRole("USER") // Ścieżki dla użytkowników
                .anyRequest().authenticated() // Wymagane uwierzytelnienie dla pozostałych stron
                .and()
                .formLogin()
                .loginPage("/login") // Strona logowania
                .defaultSuccessUrl("/", true) // Domyślne przekierowanie po zalogowaniu
                .successHandler(customAuthenticationSuccessHandler()) // Obsługa przekierowania na podstawie ról
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout") // URL wylogowania
                .logoutSuccessUrl("/index") // Przekierowanie po wylogowaniu
                .permitAll();
        return http.build();
    }


    // Obsługa przekierowania po zalogowaniu na podstawie ról
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin/main_admin"); // Przekierowanie dla administratora
            } else if (role.equals("ROLE_USER")) {
                response.sendRedirect("/user/main_user"); // Przekierowanie dla użytkownika
            } else {
                response.sendRedirect("/index"); // Domyślne przekierowanie
            }
        };
    }
}
