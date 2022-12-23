package com.motudy.account;

import com.motudy.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signUpPage(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/sign-up";
    }
}
