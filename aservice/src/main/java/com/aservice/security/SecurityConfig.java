package com.aservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new AdvertUserDetailsService();
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // authentication
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
	
	// authorization
	@Bean
	public SecurityFilterChain web(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(configurer->
								   configurer.requestMatchers("/").permitAll()
								   			 .requestMatchers("/resources/**").permitAll()
								   			 .requestMatchers("/css/**", "/js/**").permitAll()
								   			 .requestMatchers("/login/**").permitAll()
								   			 .requestMatchers("/main/creation/**").hasAnyRole("USER")
								   			 .requestMatchers("/main/").hasAnyRole("ADMIN","USER")
								   			 .requestMatchers("/admin/**").hasRole("ADMIN")
								   			 .requestMatchers("/message/**").hasAnyRole("ADMIN","USER")
											 .requestMatchers("/offer/**").hasAnyRole("ADMIN","USER").anyRequest().authenticated())
				.formLogin(configurer->configurer.loginPage("/login/showLoginPage")
						 .loginProcessingUrl("/authenticateUser")
						 .permitAll()
						 .defaultSuccessUrl("/main/", true))
				.logout(configurer -> configurer.permitAll()
						 .logoutSuccessUrl("/login/showLoginPage?logout"))
				.exceptionHandling(configurer->configurer.accessDeniedPage("/login/access-denied"))
				.build();
	}
	
}
