package org.jgayoso.ncomplo.business.services;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jgayoso.ncomplo.business.entities.Invitation;
import org.jgayoso.ncomplo.business.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGridException;

@Service
public class EmailService {

	private static final Logger logger = Logger.getLogger(EmailService.class);

	// TODO EmailService must be an interface and this a specific implementation

	@Value("${ncomplo.server.url}")
    private String baseUrl;
	
	private final SendGrid sendGrid; 
    public EmailService() {
        super();
        final Map<String, String> env = System.getenv();
    	final String username = env.get("SENDGRID_USERNAME");
    	final String password = env.get("SENDGRID_PASSWORD");
    	if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)){
    		this.sendGrid = new SendGrid(username, password);
    	} else {
    		this.sendGrid = null;
    	}
    }

	public void sendNewPassword(final User user, final String newPassword, final String baseUrl) {
		if (this.sendGrid == null) {
			logger.error("No email service found");
			return;
		}
		try {
			final Email email = new Email().setFrom("no-reply@ncomplo.com").setSubject("Your new ncomplo password");
			email.addTo(user.getEmail(), user.getName());

			// TODO This should be a thymeleaf template
			final String html = "Hello " + user.getName()
					+ "<br />To access to your ncomplo account, use your new credentials:<br><ul><li>Login: "
					+ user.getLogin() + "</li><li>Password: " + newPassword + "</li></ul>Please, change your password!"
					+ "<br /> See you soon at <a href='" + baseUrl + "'>ncomplo</a>";
			final String text = "Hello " + user.getName()
					+ "\nTo access to your ncomplo account, use your new credentials: \n-Login: " + user.getLogin()
					+ "\n-Password: " + newPassword + "\nPlease, change your password!" + "\nSee you at " + baseUrl;
			email.setHtml(html).setText(text);
			logger.debug("Sending email to " + user.getEmail());
			this.sendGrid.send(email);
			logger.debug("Reset password email sent to " + user.getEmail());

		} catch (final SendGridException e) {
			logger.error("Error sending new password email", e);
		}
	}

	public void sendInvitations(final String leagueName, final Invitation invitation, final String registerUrl, final User user) {
		if (this.sendGrid == null) {
			logger.error("Invitations: No email service found");
			return;
		}
		try {

			final Email email = new Email().setFrom("no-reply@ncomplo.com")
					.setSubject("Invitation to ncomplo league " + leagueName)
					.addTo(invitation.getEmail(), invitation.getName());

			String html = "";
			if (user == null) {
    			html = "Hello " + invitation.getName()
    					+ "<br />You have been invited to participate at the league " + leagueName + " of ncomplo<br/>"
    					+ "To create your account and sign up at the competition, click <a href='" + registerUrl
    					+ "'>here</a> and complete the registration form." + "<br/>See you soon!";
			} else {
			    html = "Hello " + invitation.getName()
                + "<br />You have been invited to participate at the league " + leagueName + " of ncomplo<br/>"
                + "To join to this league, click <a href='" + registerUrl + "'>here</a>." 
                + "<br/>If you do not remember your password, contact with " + invitation.getAdminLogin() + "<br/>See you soon!";
			}

			email.setHtml(html);
			logger.debug("Sending invitation email to " + invitation.getEmail());
			this.sendGrid.send(email);
			logger.debug("Invitation sent to " + invitation.getEmail());
		} catch (final SendGridException e) {
			logger.error("Error sending invitations", e);
		}
	}

	public void sendNotification(final String subject, final String[] destinations, final String text) {
		if (this.sendGrid == null) {
			logger.error("Notification: No email service found");
			return;
		}
		try {

			final Email email = new Email().setFrom("no-reply@ncomplo.com")
					.setSubject(subject)
					.setBcc(destinations);
			email.setHtml(text);
			logger.debug("Sending notification email");
			this.sendGrid.send(email);
			logger.debug("Notification sent");
		} catch (final SendGridException e) {
			logger.error("Error sending invitations", e);
		}
	}
}
