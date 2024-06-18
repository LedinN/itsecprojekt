package dev.nick.itsecprojekt.controller;

import dev.nick.itsecprojekt.DTOUser;
import dev.nick.itsecprojekt.PasswordUpdateDTO;
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
import org.springframework.web.util.HtmlUtils;

/*
Denna klass hanterar våra endpoints och tillhörande logik (Sparar användare till repository, "tvättar" strängar från eventuellt skadlig kod.)
 */
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
        user.setFirstname(DTOuser.getFirstname());
        user.setLastname(DTOuser.getLastname());
        user.setAge(DTOuser.getAge());
        userRepository.save(user);
        model.addAttribute("successMessage", user.getEmail() + " registered successfully");

        logger.debug("Creating user", user.getEmail());
        logger.warn("User " +  MaskingUtils.anonymize(user.getEmail()) + " was created");


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
            logger.warn("User " + MaskingUtils.anonymize(user.getEmail()) + " was deleted from database");


            return "delete_success";
        } else {
            model.addAttribute("errorMessage", escapedEmail+" not found");
            logger.info("User Not Found");
            model.addAttribute("errorMessage", email+" not found");

            logger.warn("User " + MaskingUtils.anonymize(user.getEmail()) + " not found");
            logger.debug("Debugging " + user.getEmail());

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
            logger.error("Error updating user");
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

            logger.warn("User " +  MaskingUtils.anonymize(user.getEmail()) + " was updated");

            return "update_success";
        } else {
            model.addAttribute("errorMessage", "User not found");

            return "update_user";
        }
    }

    @GetMapping("/update_password")
    public String updatePasswordForm(Model model) {
        model.addAttribute("passwordUpdateDTO", new PasswordUpdateDTO());
        return "update_password";
    }

    @PostMapping("/update_password")
    public String updatePassword(@Valid @ModelAttribute("passwordUpdateDTO") PasswordUpdateDTO passwordUpdateDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            logger.error("Password was not updated");

            return "update_password";
        }
        MyUser user = userRepository.findByEmail(passwordUpdateDTO.getEmail());
        if (user != null) {
            user.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
            userRepository.save(user);
            model.addAttribute("successMessage", "Password updated successfully");

            logger.warn("User " +  MaskingUtils.anonymize(user.getEmail()) + " update password successful");

            return "update_password_successful";
        } else {
            model.addAttribute("errorMessage", "User not found");
            return "update_password";
        }
    }

}
