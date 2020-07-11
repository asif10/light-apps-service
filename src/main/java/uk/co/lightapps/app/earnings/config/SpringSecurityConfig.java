package uk.co.lightapps.app.earnings.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import uk.co.lightapps.app.earnings.jwt.AuthEntryPoint;
import uk.co.lightapps.app.earnings.jwt.AuthRequestFilter;

import static org.springframework.http.HttpStatus.OK;

/**
 * @author Asif Akhtar
 * 25/05/2020 21:44
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    private final AuthEntryPoint authEntryPoint;
    private final UserDetailsService jwtUserDetailsService;
    private final AuthRequestFilter authRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    /* configure AuthenticationManager so that it knows from where to load
           user for matching credentials
            Use BCryptPasswordEncoder
     */
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().
                disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .permitAll()

                .antMatchers("/**/open/**").permitAll()
                .antMatchers("/**/websocket/**").permitAll()
                .antMatchers("/socket/**").permitAll()
                .antMatchers("/api/v1/logout-success").permitAll()
                .antMatchers("/secure/authenticate").permitAll()
                .anyRequest()
                .authenticated()
                .and()

                /* make sure we use stateless session; session won't be used to store user's state. */
                .exceptionHandling().authenticationEntryPoint(authEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.logout()
                .invalidateHttpSession(true)
                .logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(OK)))
                .deleteCookies("JSESSIONID");

        // Add a filter to validate the tokens with every request
        http.addFilterBefore(authRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
