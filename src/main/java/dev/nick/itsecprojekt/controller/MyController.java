package dev.nick.itsecprojekt.controller;

import dev.nick.itsecprojekt.DTOUser;
import dev.nick.itsecprojekt.persistence.MyUser;
import dev.nick.itsecprojekt.persistence.UserRepository;
import dev.nick.itsecprojekt.utils.MaskingUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class MyController {



    private static final Logger logger = LoggerFactory.getLogger(MyController.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public MyController(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new DTOUser());
        return "register";
    }

    @PostMapping("/register")

    public String register(@Valid @ModelAttribute("user") DTOUser DTOuser, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {

           logger.error("Error while creating new user");
            return "register";
        }
        MyUser user = new MyUser();
        user.setEmail(DTOuser.getEmail());
        user.setPassword(passwordEncoder.encode(DTOuser.getPassword()));
        user.setRole(DTOuser.getRole());
        userRepository.save(user);

        model.addAttribute("successMessage", user.getEmail()+" registered successfully");


        logger.debug("Creating user", user.getEmail());


        return "register_success";
    }


    @GetMapping("/")
    public String startpage() {
        return "startpage";
    }

    @GetMapping("/delete_user")
    public String remove_user() {
        return "delete_user";
    }

    @PostMapping("/delete_user")
    public String delete_user(@RequestParam("email") String email, Model model) {
        MyUser user = userRepository.findByEmail(email);
        if (user != null) {
            userRepository.delete(user);

            model.addAttribute("deletedUserEmail", user.getEmail());

            logger.info("User deleted successfully", user.getEmail());
            logger.debug("User " + MaskingUtils.anonymize(user.getEmail()) + " was deleted from database");


            return "delete_success";
        } else {
            model.addAttribute("errorMessage", email+" not found");

            logger.warn("User " + MaskingUtils.anonymize(user.getEmail()) + " not found");
            logger.debug("Debugging " + user.getEmail());

            return "delete_user";
        }
    }

}
