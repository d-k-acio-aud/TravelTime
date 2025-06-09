package com.example.travel_time.config;


import com.example.travel_time.security.JwtFilter;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;




import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
@EnableMethodSecurity
public class SecurityConfig {


    private static final Dotenv dotenv = Dotenv.configure().filename("amazon.env").load();
    private static final String SECRET = dotenv.get("SECRET");
    private static final SecretKey SECRET_KEY = new SecretKeySpec(
            SECRET.getBytes(),
            SignatureAlgorithm.HS256.getJcaName()
    );
    private final JwtFilter jwtFilter;

    private final UserDetailsService userDetailsService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/","/login", "/register", "/api/users/**",
                                "/static/**", "/api/auth/**", "/js/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout") // URL, на который будет отправлен logout-запрос
                        .logoutSuccessHandler((request, response, authentication) -> {


                            // Удалим куки, если нужно
                            Cookie cookie = new Cookie("token", null);
                            cookie.setPath("/");
                            cookie.setHttpOnly(true);
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);

                            // Редиректим на страницу входа (или любую другую)
                            response.setStatus(HttpServletResponse.SC_FOUND);
                            response.setHeader("Location", "/login");
                        })
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);;


        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8080")); // Ваш фронтенд
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // Разрешаем куки
        config.setExposedHeaders(List.of("Set-Cookie")); // Для отладки кук


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}

