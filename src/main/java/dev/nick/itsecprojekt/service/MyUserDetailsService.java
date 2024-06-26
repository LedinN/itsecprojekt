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

    /* Denna metod körs automatiskt efter att bean-initialiseringen är klar.
    Metoden skapar en administratörsanvändare om ingen sådan redan finns i databasen.
Först definieras standard användarnamn för administratören.
Den letar efter en användare med detta e-postnamn i databasen.
Om ingen användare hittas, skapas en ny MyUser-instans med fördefinierade attribut:
    E-post: adminUsername
    Lösenord: krypterat(password)
    Roll: ADMIN
    Ålder: 28
    Förnamn: Ad
    Efternamn: min
    ID: 1
    Den nya adminanvändaren sparas sedan i databasen.
*/
    @PostConstruct
    public void createAdminUser() {
        String adminUsername = "OGadmin";
        MyUser adminUser = userRepository.findByEmail(adminUsername);

        if (adminUser == null) {
            adminUser = new MyUser();
            adminUser.setEmail(adminUsername);
            adminUser.setPassword(passwordEncoder.encode("password"));
            adminUser.setRole("ADMIN");
            adminUser.setAge(28);
            adminUser.setFirstname("Ad");
            adminUser.setLastname("min");
            adminUser.setId(1L);
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
