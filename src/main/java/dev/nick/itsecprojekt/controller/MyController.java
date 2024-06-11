package dev.nick.itsecprojekt;

import dev.nick.itsecprojekt.persistence.MyUser;
import dev.nick.itsecprojekt.persistence.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MyController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public MyController(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;

    }

    @GetMapping("/")
    public String startpage(){
        return "startpage";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new DTOUser());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") DTOUser DTOuser, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            System.out.println("ERRORS"); // REPLACE WITH LOGGER
            return "register";
        }
        MyUser user = new MyUser();
        user.setEmail(DTOuser.getEmail());
        user.setPassword(passwordEncoder.encode(DTOuser.getPassword()));
        user.setRole("USER");
        userRepository.save(user);

        model.addAttribute("successMessage", user.getEmail()+" registered successfully");

        return "register_success";
    }

}
