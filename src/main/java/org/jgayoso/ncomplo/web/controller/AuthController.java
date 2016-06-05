package org.jgayoso.ncomplo.web.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jgayoso.ncomplo.business.entities.Invitation;
import org.jgayoso.ncomplo.business.entities.User;
import org.jgayoso.ncomplo.business.services.InvitationService;
import org.jgayoso.ncomplo.business.services.UserService;
import org.jgayoso.ncomplo.exceptions.InternalErrorException;
import org.jgayoso.ncomplo.web.admin.beans.UserBean;
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

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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
	public String changepassword(@RequestParam(value = "oldPassword", required = true) String oldPassword,
			@RequestParam(value = "newPassword1", required = true) String newPassword1,
			@RequestParam(value = "newPassword2", required = true) String newPassword2) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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

	@RequestMapping(method=RequestMethod.GET, value = "/invitation/{leagueId}/{emailId}")
    public String processInvitation(
    		@PathVariable("leagueId") final Integer leagueId,
    		@PathVariable("emailId") final String emailId,
    		final HttpServletRequest request, 
            final ModelMap model) {
    	
    	List<Invitation> invitations = invitationService.findByLeagueId(leagueId);
    	Invitation invitation = null;
    	for (Invitation inv: invitations) {
    		if (inv.getEmail().startsWith(emailId)) {
    			invitation = inv;
    			break;
    		}
    	}
    	
    	if (invitation == null) {
    		logger.info("Invitation not found for league " + leagueId + " and user " + emailId);
    		return "redirect:/login?error";
    	}
    	
    	final UserInvitationBean userBean = new UserInvitationBean();
    	userBean.setEmail(invitation.getEmail());
    	userBean.setName(invitation.getName());
    	userBean.setInvitationId(invitation.getId());
    	model.addAttribute("user", userBean);
    	
    	return "invitation";
    }
	
	@RequestMapping(method=RequestMethod.POST, value = "/invitation/{leagueId}/register")
	public String acceptInvitation(@PathVariable("leagueId") final Integer leagueId, 
			final UserInvitationBean userBean) {
		
		if (!userBean.getPassword().equals(userBean.getPassword2())) {
			return "invitation";
		}
		
		this.userService.registerFromInvitation(userBean.getInvitationId(), userBean.getLogin(), userBean.getName(),
				userBean.getEmail(), leagueId, userBean.getPassword());
		return "redirect:/login";
	}

}
