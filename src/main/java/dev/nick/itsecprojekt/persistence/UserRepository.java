package dev.nick.itsecprojekt.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
Gränssnitt som representerar ett repository för att hantera våra användarobjekt i databasen.
Metoden findByEmail används för att hitta en användare i databasen.
 */
@Repository
public interface UserRepository extends JpaRepository<MyUser, Long> {
    MyUser findByEmail(String email);
}


