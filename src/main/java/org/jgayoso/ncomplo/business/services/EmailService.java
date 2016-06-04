package org.jgayoso.ncomplo.business.services;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jgayoso.ncomplo.business.entities.User;
import org.springframework.stereotype.Service;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGridException;


@Service
public class EmailService {
	
	private static final Logger logger = Logger.getLogger(EmailService.class);

	private final SendGrid sendGrid; 
    public EmailService() {
        super();
        Map<String, String> env = System.getenv();
    	final String username = env.get("SENDGRID_USERNAME");
    	final String password = env.get("SENDGRID_PASSWORD");
    	if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)){
    		this.sendGrid = new SendGrid(username, password);
    	} else {
    		this.sendGrid = null;
    	}
    }
    
    public void sendNewPassword(final User user, final String newPassword) {
    	if (this.sendGrid == null){
    		logger.error("No email service found");
    		return;
    	}
    	try {
    		Email email = new Email().setFrom("no-reply@ncomplo.com").setSubject("Your new ncomplo password");
    		email.addTo(user.getEmail(), user.getName());
			String html = "Hello " + user.getName()
					+ "<br />To access to your ncomplo account user your new credentials:<br><ul><li>Login: "
					+ user.getLogin() + "</li><li>Password: " + newPassword + "</li></ul>Please, change your password!";
			String text = "Hello " + user.getName()
			+ "\nTo access to your ncomplo account user your new credentials: \n-Login: "
			+ user.getLogin() + "\n-Password: " + newPassword + "\nPlease, change your password!";
			email.setHtml(html).setText(text);
			logger.debug("Sending email to " + email.getTos());
    		this.sendGrid.send(email);
    		logger.debug("Reset password email sent to " + email.getTos());
			
		} catch (SendGridException e) {
			logger.error("Error sending new password email", e);
		}
    }
}
