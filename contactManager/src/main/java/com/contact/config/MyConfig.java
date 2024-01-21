package com.contact.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
//import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.Filter;



@Configuration
@EnableWebSecurity
public class MyConfig extends WebSecurityConfiguration {
	
//@Bean
//public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//	http
//		.authorizeHttpRequests((authorize) -> authorize
//			.anyRequest().authenticated()
//		)
//		.httpBasic(Customizer.withDefaults())
//		.formLogin(Customizer.withDefaults());
//
//	return http.build();
//}
//
//@Bean
//public UserDetailsService userDetailsService() {
//	UserDetails userDetails = User.builder()
//		.username("user")
//		.password("password")
//		.roles("USER")
//		.build();
//
//	return new InMemoryUserDetailsManager(userDetails);
//}

@Bean
public UserDetailsService getUserDetailService() {
	return new UserDetailsServiceImpl();
}
	
@Bean
public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

@Bean
public DaoAuthenticationProvider  authenticationProvider()
{
	DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
	daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());
	daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
	
	return daoAuthenticationProvider;
}



protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth.authenticationProvider(authenticationProvider());

}

//@Bean
//public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//    return authenticationConfiguration.authenticationProvider(authenticationProvider());
//}

//@Bean	
//protected void configure(HttpSecurity http) throws Exception {
//	http.authorizeRequests().requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/user/**").hasRole("USER")
//			.requestMatchers("/**").permitAll().and().formLogin()
//			.loginPage("/signin")
//			.loginProcessingUrl("/dologin")
//			.defaultSuccessUrl("/user/index")				
//			.and().csrf().disable();
//}

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

  http.authorizeRequests()
      .requestMatchers("/user/**").hasRole("USER")
      .requestMatchers("/**").permitAll().and().formLogin()
      .loginPage("/signin")
		.loginProcessingUrl("/dologin")
		.defaultSuccessUrl("/user/index")				
		.and().csrf().disable();
      return http.build();
}

	
}
