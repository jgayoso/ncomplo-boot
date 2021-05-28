package org.jgayoso.ncomplo.business.services;

import java.io.IOException;
import java.util.Locale;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jgayoso.ncomplo.business.entities.Invitation;
import org.jgayoso.ncomplo.business.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.sendgrid.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

	private static final Logger logger = Logger.getLogger(EmailService.class);

	// TODO EmailService must be an interface and this a specific implementation

	@Value("${ncomplo.server.url}")
    private String baseUrl;

	@Autowired
	private TemplateEngine templateEngine;
	@Autowired
	protected MessageSource resource;

	private final SendGrid sendGrid;
	private final String fromEmail;

    public EmailService() {
        super();
    	final String apiKey = System.getenv("SENDGRID_API_KEY");
    	if (StringUtils.isNotBlank(apiKey)){
    		this.sendGrid = new SendGrid(apiKey);
    	} else {
    		this.sendGrid = null;
    	}

		fromEmail = System.getenv("EMAIL_FROM");
    }

	public void sendNewPassword(final User user, final String newPassword, final String baseUrl) {
		if (this.sendGrid == null) {
			logger.error("No email service found");
			return;
		}
		try {
			Email email = new Email(fromEmail);
			String subject = "Your new ncomplo password";

			Email to = new Email(user.getEmail(), user.getName());

//			// TODO This should be a thymeleaf template
			final String html = "Hello " + user.getName()
					+ "<br />To access to your ncomplo account, use your new credentials:<br><ul><li>Login: "
					+ user.getLogin() + "</li><li>Password: " + newPassword + "</li></ul>Please, change your password!"
					+ "<br /> See you soon at <a href='" + baseUrl + "'>ncomplo</a>";
			final String text = "Hello " + user.getName()
					+ "\nTo access to your ncomplo account, use your new credentials: \n-Login: " + user.getLogin()
					+ "\n-Password: " + newPassword + "\nPlease, change your password!" + "\nSee you at " + baseUrl;

			Content content = new Content("text/html", html);
			Content textContent = new Content("text/plain", text);

			Mail mail = new Mail(email, subject, to, content);
			mail.addContent(textContent);

			logger.info("Sending new password email to " + user.getEmail());
			sendMailRequest(mail);
			logger.info("Reset password email sent to " + user.getEmail());

		} catch (final IOException e) {
			logger.error("Error sending new password email", e);
		}
	}

	public void sendInvitations(final String leagueName, final Invitation invitation, final String registerUrl,
								final User user, final Locale locale) {
		if (this.sendGrid == null) {
			logger.error("Invitations: No email service found");
			return;
		}
		try {
			String[] subjectParams = {leagueName};

			final String emailSubject = resource.getMessage("emails.invitation.subject", subjectParams, locale);
			final Email email = new Email(fromEmail);
			Email to = new Email(invitation.getEmail(), invitation.getName());

			final Context ctx = new Context(locale);
			ctx.setVariable("invitationName", invitation.getName());
			ctx.setVariable("leagueName", leagueName);
			ctx.setVariable("url", registerUrl);
			ctx.setVariable("isNewUser", user != null);

			final String html = this.templateEngine.process("emails/invitation", ctx);
			Content content = new Content("text/html", html);
			logger.debug("Sending invitation email to " + invitation.getEmail());

			Mail mail = new Mail(email, emailSubject, to, content);
			sendMailRequest(mail);
			logger.debug("Invitation sent to " + invitation.getEmail());
		} catch (final IOException e) {
			logger.error("Error sending invitations", e);
		}
	}

	public void sendNotification(final String subject, final String[] destinations, final String text) {
		if (this.sendGrid == null) {
			logger.error("Notification: No email service found");
			return;
		}
		try {

			final Email from = new Email(fromEmail);
			Content content = new Content("text/html", text);
			logger.info("Sending notification email");
			Mail mail = new Mail();
			mail.setFrom(from);
			mail.setSubject(subject);
			mail.addContent(content);

			for (String destination: destinations) {
				Email toEmail = new Email(destination);
				Personalization pers = new Personalization();
				pers.addTo(toEmail);
				mail.addPersonalization(pers);
			}

			sendMailRequest(mail);
			logger.info("Notification sent");
		} catch (final IOException e) {
			logger.error("Error sending invitations", e);
		}
	}

	private void sendMailRequest(Mail mail) throws IOException{
		Request request = new Request();
		request.setMethod(Method.POST);
		request.setEndpoint("mail/send");
		request.setBody(mail.build());
		Response response = this.sendGrid.api(request);
		logger.info("Response code " + response.getStatusCode());
		logger.info(response.getBody());
		logger.info(response.getHeaders());
	}
}
