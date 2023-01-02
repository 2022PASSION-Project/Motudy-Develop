package com.motudy.main;

import com.motudy.account.CurrentAccount;
import com.motudy.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    /** 현재 인증된 유저가 anonymous라면 account에는 null이 들어감 */
    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        if(account != null) {
            model.addAttribute(account);
        }
        return "index"; // templates/index
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login
    }
}
