package org.jgayoso.ncomplo.web.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jgayoso.ncomplo.business.entities.ForgotPasswordToken;
import org.jgayoso.ncomplo.business.entities.Invitation;
import org.jgayoso.ncomplo.business.entities.User;
import org.jgayoso.ncomplo.business.services.InvitationService;
import org.jgayoso.ncomplo.business.services.UserService;
import org.jgayoso.ncomplo.exceptions.LeagueClosedException;
import org.jgayoso.ncomplo.web.admin.beans.UserInvitationBean;
import org.jgayoso.ncomplo.web.beans.ResetPasswordBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

	private static final Logger logger = Logger.getLogger(AuthController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private InvitationService invitationService;

	public AuthController() {
		super();
	}

	@RequestMapping("/resetpassword")
	public String resetPassword(final ModelMap model, 
	        @RequestParam(value = "login", required = true) final String login, 
	        @RequestParam(value = "email", required = true) final String email,
	        final RedirectAttributes redirectAttributes) {
	    
	    final User userByLogin = this.userService.find(login);
	    final User userByEmail = this.userService.findByEmail(email);
	    if (userByLogin != null && userByEmail != null && userByLogin.getLogin().equals(userByEmail.getLogin())) {
	        model.addAttribute("userInfo", new ResetPasswordBean(login, email));
	        return "resetpassword";
	    } else {
	        redirectAttributes.addFlashAttribute("error", "Invalid reset password url");
	        return "redirect:/login?error";
	    }
	    
	}
	
	@RequestMapping("/resetpassword-confirm")
    public String resetPasswordConfirm(final ResetPasswordBean bean, final RedirectAttributes redirectAttributes) {
        
	    this.userService.resetPassword(bean.getLogin(), true);
	    redirectAttributes.addFlashAttribute("message", "A new password has been sent to your email");
	    return "redirect:/login";
        
    }

	@RequestMapping("/password")
	public String password(final ModelMap model) {

		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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
			@RequestParam(value = "oldPassword", required = true) final String oldPassword,
			@RequestParam(value = "newPassword1", required = true) final String newPassword1,
			@RequestParam(value = "newPassword2", required = true) final String newPassword2,
			final RedirectAttributes redirectAttributes) {

		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof AnonymousAuthenticationToken) {
			/* The user is not logged in */
			return "login";
		}
		/* The user is logged in */
		final String login = auth.getName();

		if (!newPassword1.equals(newPassword2)) {
			redirectAttributes.addFlashAttribute("error", "Passwords don't match");
			return "redirect:/password";
		}

		this.userService.changePassword(login, oldPassword, newPassword1);

		return "redirect:/scoreboard";

	}

	@RequestMapping(method=RequestMethod.GET, value = "/joinLeague/{leagueToken}")
	public String processInvitationGroup(@PathVariable("leagueToken") final String token,
			final ModelMap model, final RedirectAttributes redirectAttributes) {
		
		final Invitation invitation = this.invitationService.findByToken(token);
		if (invitation == null) {
            logger.info("Invalid invitation " + token);
            redirectAttributes.addFlashAttribute("error", "Invalid invitation");
            return "redirect:/login?error";
    	}
		
		final UserInvitationBean userBean = new UserInvitationBean();
    	userBean.setEmail(invitation.getEmail());
    	userBean.setName(invitation.getName());
    	userBean.setInvitationId(invitation.getId());
    	model.addAttribute("invitation", userBean);
    	
    	return "invitation";
		
	}
	
	@RequestMapping(method=RequestMethod.GET, value = "/invitation/{invitationId}/{leagueId}/{emailId}")
    public String processInvitation(
            @PathVariable("invitationId") final Integer invitationId,
    		@PathVariable("leagueId") final Integer leagueId,
    		@PathVariable("emailId") final String emailId,
            final ModelMap model, final RedirectAttributes redirectAttributes) {
    	
	    final Invitation invitation = this.invitationService.findById(invitationId);
        if (invitation == null || !invitation.getLeague().getId().equals(leagueId)
                || !invitation.getEmail().startsWith(emailId)) {
            logger.info("Invalid invitation " + leagueId + " for values " + invitationId + ", " + leagueId + ", "
                    + emailId);
            redirectAttributes.addFlashAttribute("error", "Invalid invitation");
            return "redirect:/login?error";
    	}
        
        final User user = this.userService.findByEmail(invitation.getEmail());
        if (user != null) {
            this.userService.acceptInvitation(invitationId, leagueId, user);
            redirectAttributes.addFlashAttribute("message", "You have joined to the league successfully");
            return "redirect:/scoreboard";
        }
    	
    	final UserInvitationBean userBean = new UserInvitationBean();
    	userBean.setEmailId(emailId);
    	userBean.setEmail(invitation.getEmail());
    	userBean.setName(invitation.getName());
    	userBean.setInvitationId(invitation.getId());
    	model.addAttribute("invitation", userBean);
    	
    	return "invitation";
    }
	
	@RequestMapping(method=RequestMethod.POST, value = "/invitation/{invitationId}/{leagueId}/register")
	public String acceptInvitation(
	        @PathVariable("invitationId") final Integer invitationId,
	        @PathVariable("leagueId") final Integer leagueId, 
			final UserInvitationBean userBean, final RedirectAttributes redirectAttributes) {
		
		if (!userBean.getPassword().equals(userBean.getPassword2())) {
            redirectAttributes.addFlashAttribute("error", "Passwords don't match");
			return "redirect:/invitation/"+invitationId+"/"+leagueId+"/"+userBean.getEmailId();
		}
		
		try {
			this.userService.registerFromInvitation(invitationId, userBean.getLogin(), userBean.getName(),
					userBean.getEmail(), leagueId, userBean.getPassword());
		} catch (final LeagueClosedException e) {
			redirectAttributes.addFlashAttribute("error", "League is closed");
			return "redirect:/login";
		}
		redirectAttributes.addFlashAttribute("message", "You have joined to the league successfully");
		return "redirect:/login";
	}
	
	@RequestMapping(method=RequestMethod.POST, value = "/joinLeague/register")
	public String acceptInvitationGroup(
			final UserInvitationBean userBean,
			final RedirectAttributes redirectAttributes) {
		
		final Invitation invitation = this.invitationService.findById(userBean.getInvitationId());
		if (invitation == null) {
			logger.info("Invalid invitation");
            redirectAttributes.addFlashAttribute("error", "Invalid invitation");
            return "redirect:/login?error";
		}
		
		if (!userBean.getPassword().equals(userBean.getPassword2())) {
            redirectAttributes.addFlashAttribute("error", "Passwords don't match");
			return "redirect:/joinLeague/"+invitation.getToken();
		}
		try {
			this.userService.registerFromInvitation(invitation.getId(), userBean.getLogin(), userBean.getName(),
					userBean.getEmail(), invitation.getLeague().getId(), userBean.getPassword());
		} catch (final LeagueClosedException e) {
			redirectAttributes.addFlashAttribute("error", "League is closed");
			return "redirect:/login";
		}
		
		return "redirect:/login";
	}

	@RequestMapping("/forgot-password")
	public String forgotPassword(final ModelMap model) {
		model.addAttribute("userInfo", new ResetPasswordBean());
		return "forgotpassword";
	}

	@RequestMapping("/forgot-password-confirm")
	public String forgotPasswordConfirm(final ResetPasswordBean bean, final RedirectAttributes redirectAttributes) {

		if (StringUtils.isBlank(bean.getEmail())) {
			redirectAttributes.addFlashAttribute("error", "Empty email address");
			return "redirect:/login?error";
		}

		this.userService.forgotPassword(bean.getEmail());
		return "redirect:/login";
	}

	@RequestMapping(method=RequestMethod.GET, value = "/forgot-password-reset/{login}/{token}")
	public String forgotPasswordRequest(
			@PathVariable("login") final String login,
			@PathVariable("token") final String token,
			final ModelMap model, final RedirectAttributes redirectAttributes) {

		User user = this.userService.find(login);
		if (user != null) {
			ForgotPasswordToken fpt = this.userService.findForgotPasswordToken(user);
			if (fpt != null && StringUtils.equalsIgnoreCase(token, fpt.getToken())) {
				model.addAttribute("user", user);
				return "forgotpasswordreset";

			}
		}
		redirectAttributes.addFlashAttribute("error", "Invalid url");
		return "redirect:/login?error";




	}

	@RequestMapping("/forgot-password-change")
	public String changeForgotPassword(
			@RequestParam(value = "login", required = true) final String login,
			@RequestParam(value = "newPassword1", required = true) final String newPassword1,
			@RequestParam(value = "newPassword2", required = true) final String newPassword2,
			final RedirectAttributes redirectAttributes) {

		if (!newPassword1.equals(newPassword2)) {
			redirectAttributes.addFlashAttribute("error", "Passwords don't match");
			return "redirect:/password";
		}

		User user = this.userService.find(login);
		if (user == null) {
			redirectAttributes.addFlashAttribute("error", "Invalid request");
			return "redirect:/login?error";
		}

		ForgotPasswordToken fpt = this.userService.findForgotPasswordToken(user);
		if (fpt == null) {
			return "redirect:/login?error";
		}

		this.userService.changePassword(login, newPassword1, fpt);

		return "redirect:/login";

	}

}
