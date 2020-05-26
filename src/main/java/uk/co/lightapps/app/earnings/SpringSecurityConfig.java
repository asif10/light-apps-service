package uk.co.lightapps.app.earnings;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import static org.springframework.http.HttpStatus.OK;

/**
 * @author Asif Akhtar
 * 25/05/2020 21:44
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().
                disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/api/v1/logout-success").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
                .and()
                .logout()
                .permitAll();

        http.logout()
                .invalidateHttpSession(true)
                .logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(OK)))
                .deleteCookies("JSESSIONID");

    }
}
