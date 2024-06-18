package dev.nick.itsecprojekt.service;

import dev.nick.itsecprojekt.DTOUser;
import dev.nick.itsecprojekt.persistence.MyUser;
import dev.nick.itsecprojekt.persistence.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/*
  UserService är en klass som ansvarar för att hantera användaruppdateringar i systemet.

 Denna klass använder sig av UserRepository för att interagera med databasen och en PasswordEncoder
  för att hantera kryptering av lösenord.

 Metoder:
updateUser(DTOUser dtoUser): Uppdaterar en användares information baserat på data från ett DTOUser-objekt.
Om användaren inte finns i databasen kastas ett exception.

 */
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
}
