package dev.nick.itsecprojekt.service;

import dev.nick.itsecprojekt.persistence.MyUser;
import dev.nick.itsecprojekt.persistence.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;


@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MyUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @PostConstruct
    public void createAdminUser() {
        String adminUsername = "OGadmin";
        MyUser adminUser = userRepository.findByEmail(adminUsername);

        if (adminUser == null) {
            adminUser = new MyUser();
            adminUser.setEmail(adminUsername);
            adminUser.setPassword(passwordEncoder.encode("password"));
            adminUser.setRole("ADMIN");
            userRepository.save(adminUser);
        }
    }

    @Override
    public User loadUserByUsername(final String username) throws UsernameNotFoundException {
        MyUser user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user found with username: " + username);
        }
        Collection<? extends GrantedAuthority> authorities = getAuthorities(user.getRole());
        return new User(user.getEmail(), user.getPassword(), true, true, true, true, authorities);
    }
}
