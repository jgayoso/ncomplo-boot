package org.jgayoso.ncomplo.web.controller;

import org.apache.log4j.Logger;
import org.jgayoso.ncomplo.business.entities.Invitation;
import org.jgayoso.ncomplo.business.entities.User;
import org.jgayoso.ncomplo.business.services.InvitationService;
import org.jgayoso.ncomplo.business.services.UserService;
import org.jgayoso.ncomplo.exceptions.InternalErrorException;
import org.jgayoso.ncomplo.web.admin.beans.UserInvitationBean;
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
	public String changepassword(@RequestParam(value = "oldPassword", required = true) final String oldPassword,
			@RequestParam(value = "newPassword1", required = true) final String newPassword1,
			@RequestParam(value = "newPassword2", required = true) final String newPassword2) {

		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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
    	
    	final UserInvitationBean userBean = new UserInvitationBean();
    	userBean.setEmail(invitation.getEmail());
    	userBean.setName(invitation.getName());
    	userBean.setInvitationId(invitation.getId());
    	model.addAttribute("user", userBean);
    	
    	return "invitation";
    }
	
	@RequestMapping(method=RequestMethod.POST, value = "/invitation/{invitationId}/{leagueId}/register")
	public String acceptInvitation(
	        @PathVariable("invitationId") final Integer invitationId,
	        @PathVariable("leagueId") final Integer leagueId, 
			final UserInvitationBean userBean, final RedirectAttributes redirectAttributes) {
		
		if (!userBean.getPassword().equals(userBean.getPassword2())) {
		    redirectAttributes.addFlashAttribute("error", "Passwords don't match");
			return "invitation";
		}
		
		this.userService.registerFromInvitation(invitationId, userBean.getLogin(), userBean.getName(),
				userBean.getEmail(), leagueId, userBean.getPassword());
		return "redirect:/login";
	}

}
