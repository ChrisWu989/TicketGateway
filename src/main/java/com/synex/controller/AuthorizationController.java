package com.synex.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class AuthorizationController {

	@PreAuthorize("permitAll()")
    @GetMapping("/login")
    public String login() {
    	System.out.println("LOGIN CONTROLLER HIT");
        return "login";
    }
    
    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return "PING OK";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            return "redirect:/dashboard/admin";
        }
        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("MANAGER"))) {
            return "redirect:/dashboard/manager";
        }
        return "redirect:/dashboard/user";
    }
    
    @GetMapping("/dashboard/user")
    public String user() {
        return "user";
    }

    @GetMapping("/dashboard/manager")
    public String manager() {
        return "manager";
    }

    @GetMapping("/dashboard/admin")
    public String admin() {
        return "admin";
    }
}
