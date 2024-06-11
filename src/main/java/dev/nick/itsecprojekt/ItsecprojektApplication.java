package dev.nick.itsecprojekt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"dev.nick.itsecprojekt.persistence", "dev.nick.itsecprojekt"})
public class ItsecprojektApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItsecprojektApplication.class, args);
    }
}


