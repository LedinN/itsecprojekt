package dev.nick.itsecprojekt.controller;

import dev.nick.itsecprojekt.DTOUser;
import dev.nick.itsecprojekt.PasswordUpdateDTO;
import dev.nick.itsecprojekt.persistence.MyUser;
import dev.nick.itsecprojekt.persistence.UserRepository;
import dev.nick.itsecprojekt.service.UserService;
import dev.nick.itsecprojekt.utils.MaskingUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final UserService userService;

    public MyController(PasswordEncoder passwordEncoder, UserRepository userRepository, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
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

        userService.registerUser(DTOuser);
        model.addAttribute("successMessage", DTOuser.getEmail() + " registered successfully");

        logger.debug("Creating user", DTOuser.getEmail());
        logger.warn("User " + MaskingUtils.anonymize(DTOuser.getEmail()) + " was created");

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

        try {
            userService.deleteUser(escapedEmail);

            model.addAttribute("deletedUserEmail", escapedEmail);
            logger.info("User deleted successfully", escapedEmail);
            logger.warn("User " + MaskingUtils.anonymize(escapedEmail) + " was deleted from database");

            return "delete_success";
        } catch (Exception UsernameNotFoundException) {

            model.addAttribute("errorMessage", escapedEmail + " not found");
            logger.info("User Not Found");
            model.addAttribute("errorMessage", escapedEmail + " not found");
            logger.warn("User " + MaskingUtils.anonymize(escapedEmail) + " not found");
            logger.debug("Debugging " + escapedEmail);

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

        try {
            userService.updateUser(DTOuser);
            model.addAttribute("successMessage", "User updated successfully");
            logger.warn("User " + MaskingUtils.anonymize(DTOuser.getEmail()) + " was updated");
            return "update_success";
        } catch (IllegalArgumentException e) {
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

        try {
            userService.updatePassword(passwordUpdateDTO);
            model.addAttribute("successMessage", "Password updated successfully");
            logger.warn("User " + MaskingUtils.anonymize(passwordUpdateDTO.getEmail()) + " update password successful");
            return "update_password_successful";
        } catch (UsernameNotFoundException e) {
            model.addAttribute("errorMessage", "User not found");
            return "update_password";
        }
    }
}