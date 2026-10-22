package br.com.fatecads.fatecads.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/css/**",
                                "/error",
                                "/usuarios",
                                "/usuarios/novo",
                                "/usuarios/salvar",
                                "/recuperar-senha",
                                "/redefinir-senha"
                        ).permitAll()
                        // USER: navegar pelos produtos, usar o carrinho e ver o historico de compras
                        .requestMatchers(
                                HttpMethod.GET,
                                "/",
                                "/fatecads",
                                "/produtos",
                                "/produtos/listar",
                                "/produtos/*/imagem",
                                "/carrinho",
                                "/pedidos"
                        ).hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/carrinho/*").hasAnyRole("ADMIN", "USER")
                        // ADMIN: todo o resto (cadastros, edicao, exclusao, h2-console)
                        .anyRequest().hasRole("ADMIN")
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/fatecads", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
