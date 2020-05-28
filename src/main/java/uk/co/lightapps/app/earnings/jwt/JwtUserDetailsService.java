package uk.co.lightapps.app.earnings.jwt;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Used by the authenticationManager to validate the passed in user details with this class.
 * Used by the JWTRequesttFilter to get user details which will then be passed to another
 * class witth the token for validation
 *
 * @author Asif Akhtar
 * 28/05/2020 20:54
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("user".equals(username)) {
            return new User("user", "$2a$10$1uDULv9lbtvZ3t9znSfK1O5evmZUjT8WCjvg0Nw.UvrfiU/5n92My", new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}