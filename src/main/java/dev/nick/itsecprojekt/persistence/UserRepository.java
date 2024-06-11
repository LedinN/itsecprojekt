package dev.nick.itsecprojekt.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;


public interface UserRepository extends JpaRepository<MyUser, Long> {
    MyUser findByEmail(String email);
}
