package dev.nick.itsecprojekt.service;

import dev.nick.itsecprojekt.DTOUser;
import dev.nick.itsecprojekt.persistence.MyUser;
import dev.nick.itsecprojekt.persistence.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void updateUser(DTOUser dtoUser) {
        MyUser user = userRepository.findByEmail(dtoUser.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        user.setFirstname(dtoUser.getFirstname());
        user.setLastname(dtoUser.getLastname());
        user.setAge(dtoUser.getAge());
        if (dtoUser.getPassword() != null && !dtoUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dtoUser.getPassword()));
        }
        user.setRole(dtoUser.getRole());
        userRepository.save(user);
    }

    public void registerUser(DTOUser dtoUser) {
        String sanitizedEmail = HtmlUtils.htmlEscape(dtoUser.getEmail());
        MyUser user = new MyUser();
        user.setEmail(sanitizedEmail);
        user.setPassword(passwordEncoder.encode(dtoUser.getPassword()));
        user.setRole(dtoUser.getRole());
        user.setFirstname(dtoUser.getFirstname());
        user.setLastname(dtoUser.getLastname());
        user.setAge(dtoUser.getAge());
        userRepository.save(user);
    }
}
