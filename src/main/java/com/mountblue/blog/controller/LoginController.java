package com.mountblue.blog.controller;

import com.mountblue.blog.entitites.User;
import com.mountblue.blog.repository.UserRepository;
import com.mountblue.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class LoginController {

    @Autowired
    UserService userService;
    @GetMapping("/showMyLoginPage")
    public String showMyLoginPage(){

        return "fancy-login";

    }

    @PostMapping("/showMyLoginPage")
    public String checkLogin(@RequestParam("name") String name,
                             @RequestParam("password") String password){
        List<User> userList = userService.getAllUser();

        boolean check = false;

        for(User user : userList){
            if(name.equals(user.getName()) && password.equals(user.getPassword())){
                check = true;
                break;
            }
        }

        if(check == true){
            return "redirect:/posts/list";
        }
        else{
            return "fancy-login";
        }

    }

    // add request mapping for /access-denied
    @GetMapping("/access-denied")
    public String showAcessDenied(){

        return "access-denied";

    }

    @GetMapping("/signup")
    public String showSignUp(){

        return "signup";
    }


    @PostMapping("/signup")
    public String saveUser(@ModelAttribute User user){
        userService.saveUser(user);
        return "redirect:/showMyLoginPage";
    }


}
