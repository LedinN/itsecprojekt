package dev.nick.itsecprojekt.controller;

import dev.nick.itsecprojekt.DTOUser;
import dev.nick.itsecprojekt.persistence.MyUser;
import dev.nick.itsecprojekt.persistence.UserRepository;
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
import org.springframework.web.util.HtmlUtils;


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

        String sanitizedEmail = HtmlUtils.htmlEscape(DTOuser.getEmail());

        MyUser user = new MyUser();
        user.setEmail(sanitizedEmail);
        user.setPassword(passwordEncoder.encode(DTOuser.getPassword()));
        user.setRole(DTOuser.getRole());
        userRepository.save(user);

        model.addAttribute("successMessage", user.getEmail()+" registered successfully");

        logger.info("User registered successfully", user.getEmail());


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
        String escapedEmail = HtmlUtils.htmlEscape(email);
        MyUser user = userRepository.findByEmail(escapedEmail);
        if (user != null) {
            userRepository.delete(user);
            model.addAttribute("deletedUserEmail", user.getEmail());
            logger.info("User deleted successfully", user.getEmail());
            return "delete_success";
        } else {
            model.addAttribute("errorMessage", escapedEmail+" not found");
            logger.info("User Not Found");
            return "delete_user";
        }
    }

    @GetMapping("/update_user")
    public String updateUserForm(Model model) {
        model.addAttribute("user", new DTOUser());
        return "update_user";
    }

    @PostMapping("/update_user")
    public String updateUser(@Valid @ModelAttribute("user") DTOUser DTOuser, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "update_user";
        }
        MyUser user = userRepository.findByEmail(DTOuser.getEmail());
        if (user != null) {
            user.setPassword(passwordEncoder.encode(DTOuser.getPassword()));
            user.setRole(DTOuser.getRole());
            user.setFirstname(DTOuser.getFirstname());
            user.setLastname(DTOuser.getLastname());
            user.setAge(DTOuser.getAge());
            userRepository.save(user);
            model.addAttribute("successMessage", "User updated successfully");
            return "update_success";
        } else {
            model.addAttribute("errorMessage", "User not found");
            return "update_user";
        }
    }
}
