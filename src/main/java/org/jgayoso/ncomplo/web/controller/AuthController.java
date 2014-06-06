package org.jgayoso.ncomplo.web.controller;

import org.jgayoso.ncomplo.business.entities.User;
import org.jgayoso.ncomplo.business.services.UserService;
import org.jgayoso.ncomplo.exceptions.InternalErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    
    
    @Autowired
    private UserService userService;
    
    
    public AuthController() {
        super();
    }
    

    @RequestMapping("/password")
    public String password(final ModelMap model) {
        
    	Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
			/* The user is not logged in */
			return "login";
		}
		/* The user is logged in */
		final String login = auth.getName();
		
        final User user = this.userService.find(login);
        
        model.addAttribute("user", user);
        
        return "changepassword";
        
    }


    @RequestMapping("/changepassword")
    public String changepassword(
            @RequestParam(value="oldPassword",required=true) String oldPassword,
            @RequestParam(value="newPassword1",required=true) String newPassword1,
            @RequestParam(value="newPassword2",required=true) String newPassword2) {
        
    	Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
			/* The user is not logged in */
			return "login";
		}
		/* The user is logged in */
		final String login = auth.getName();
        
        if (!newPassword1.equals(newPassword2)) {
            throw new InternalErrorException("New passwords do not match!");
        }

        this.userService.changePassword(login, oldPassword, newPassword1);
        
        return "redirect:/scoreboard";
        
    }

    
}
