package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.demo.service.UserService;




@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	//** User service */
    @Autowired
    private UserService userService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
    	
        // Get AuthenticationManager Bean
        return super.authenticationManagerBean();
        
    }

    /**
     * Password Encoder the password of user
     * 
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
    	
        return new BCryptPasswordEncoder();
        
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	
    	// Provide user service for spring security
        auth.userDetailsService(userService) 
            .passwordEncoder(passwordEncoder());
        
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                    .and()
                .csrf()
                    .disable()
                .authorizeRequests()
                    .antMatchers("/login").permitAll()
//                    .antMatchers("/admin/**").hasAuthority("admin")
//                    .antMatchers("/employee/**").hasAnyAuthority("admin","employee")
//                    .antMatchers("/upload").hasAnyAuthority("admin","employee")
                    .anyRequest().permitAll();

        // Add filter check JWT
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }
}