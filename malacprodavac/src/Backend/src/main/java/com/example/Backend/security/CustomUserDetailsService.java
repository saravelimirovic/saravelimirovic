package com.example.Backend.security;

import com.example.Backend.entity.User;
import com.example.Backend.repository.UserRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;

import static com.example.Backend.webdomain.PermissionsCalculator.calculateUserRoles;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service("customUserDetailsService")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CustomUserDetailsService implements UserDetailsService {
    private static final String INVALID_CREDENTIALS = "Invalid email address or password";
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String credentials) throws UsernameNotFoundException {

        if (credentials == null || isEmpty(credentials)) {
            throw new UsernameNotFoundException(INVALID_CREDENTIALS);
        }

        User user = userRepository.findByEmail(credentials)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials "+credentials));

        return prepareLoginDetails(credentials, user.getEmail());
    }

    private UserDetails prepareLoginDetails(String credentials, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(INVALID_CREDENTIALS));

        CustomUserDetails userDetails = new CustomUserDetails(user, credentials);

        prepareUserDetails(userDetails);

        return userDetails;
    }

    private void prepareUserDetails(CustomUserDetails userDetails) {
        Optional<User> maybeKorisnik = userRepository.findByEmail(userDetails.getEmail());
        if (maybeKorisnik.isPresent()) {
            User user = maybeKorisnik.get();
            userDetails.setRoles(calculateUserRoles(user));
        }
    }
}
