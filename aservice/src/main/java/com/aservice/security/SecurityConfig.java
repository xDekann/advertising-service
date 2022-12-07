package com.aservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
	// tworzy fabryke do pobierania informacji o uzytkownikach podczas logowania
	@Bean
	public UserDetailsService userDetailsService() {
		return new AdvertUserDetailsService();
	}
	// tworzy obiekt do szyfrowania hasla
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // tworzy obiekt, ktorego spring uzywa w tle do uwierzytelniania uzytkownika (uwierzytelnianie, nie autoryzacja)
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
	// tworzenie obiektu do autoryzacji	
	@Bean
	public SecurityFilterChain web(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(configurer->
								   configurer.requestMatchers("/").permitAll()
								   			 .requestMatchers("/resources/**").permitAll()
								   			 .requestMatchers("/login/**").permitAll() // wszyscy maja dostep (takze i nie zalogowani) do calej zawartosci LoginController
								   			 .requestMatchers("/main/**").hasAnyRole("ADMIN","USER") // zezwalam na dostep adminom i userom do /main (MainController) i podsciezek
								   			 .requestMatchers("/panel/**").hasAnyRole("ADMIN","USER")
											 .requestMatchers("/offer/**").hasAnyRole("ADMIN","USER").anyRequest().authenticated()) // kazdy inny request wymaga authentication
				.formLogin(configurer->configurer.loginPage("/login/showLoginPage")
						 .loginProcessingUrl("/authenticateUser") // url do ktorego przejdziemy po sukcesywnym zalogowaniu sie (nie ma go w controllerach) i od razu zostanie wywolany defaultSuccessUrl, chyba jakies domyslne API
						 .permitAll()
						 .defaultSuccessUrl("/main/", true))
				.logout(configurer -> configurer.permitAll()
						 .logoutSuccessUrl("/login/showLoginPage?logout"))
				.exceptionHandling(configurer->configurer.accessDeniedPage("/login/access-denied"))// chyba dziala tylko jak ktos jest zalogowany
				.csrf().disable() // TO DO ENABLE
				.build();
	}
	
}
