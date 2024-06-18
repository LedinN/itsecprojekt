package dev.nick.itsecprojekt.config;

import dev.nick.itsecprojekt.persistence.UserRepository;
import dev.nick.itsecprojekt.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//Konfigurerar säkerhetsinställningarna för Spring.
@Configuration
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;


 /*
 Konfigurerar logiken för vilka roller som får komma åt vilka endpoints.
 Konfigurerar formulärinloggning med URL:er för lyckad och misslyckad inloggning.
  */
    @Bean
    public SecurityFilterChain securityChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .requestMatchers("/remove_user"
                                        , "/update_user"
                                        ,"/register"
                                        ,"/update_password"
                                        ,"/delete_user"
                                        ,"/register_success")
                                .hasRole("ADMIN")
                                .requestMatchers("/").hasAnyRole("USER","ADMIN")
                                .requestMatchers("/login", "/logout").permitAll()
                                .anyRequest().authenticated()
                )
        .formLogin(
                        formLogin -> formLogin
                                .defaultSuccessUrl("/", true)
                                .failureUrl("/login?error=true")
                                .permitAll()
                ).csrf(Customizer.withDefaults());
        return http.build();
    }

    // Hashar lösenord
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // Tar in en anpassad variant av UserDetailsService.
    @Bean
    public UserDetailsService userDetailsService() {
        return new MyUserDetailsService(userRepository, passwordEncoder());
    }
    // Konfigurerar AuthenticationManager med en DaoAuthenticationProvider
    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http, DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
        AuthenticationManager manager = http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(daoAuthenticationProvider)
                .build();
        return manager;
    }
    // Konfigurerar en DaoAuthenticationProvider med ett UserDetailsService och en PasswordEncoder
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}

