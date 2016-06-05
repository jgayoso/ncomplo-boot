package org.jgayoso.ncomplo.business.services;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jgayoso.ncomplo.business.entities.Invitation;
import org.jgayoso.ncomplo.business.entities.League;
import org.jgayoso.ncomplo.business.entities.User;
import org.jgayoso.ncomplo.business.entities.repositories.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InvitationService {
	
	private static final Logger logger = Logger.getLogger(InvitationService.class);
	
	@Autowired
    private InvitationRepository invitationRepository;
	
	@Autowired
	private LeagueService leagueService;
	@Autowired
	private UserService userService;
	@Autowired
	private EmailService emailService;
	
	@Value("${ncomplo.server.url}")
	private String baseUrl;
	
	public InvitationService() {
		super();
	}
	
	public List<Invitation> findByLeagueId(final Integer leagueId) {
		return this.invitationRepository.findByLeagueId(leagueId);
	}
	
	public Invitation findByLeagueIdAndEmail(final Integer leagueId, final String email) {
		return this.invitationRepository.findByLeagueIdAndEmail(leagueId, email);
	}
	
	public void sendInvitations(final Integer leagueId, final String adminLogin, final String name, final String email) {
		League league = this.leagueService.find(leagueId);
		if (league == null) {
			logger.info("Trying to send an invitation for a non existent league " + leagueId);
		}
		
		User existentUser = userService.findByEmail(email);
		if (existentUser != null) {
			logger.info("Invitation cannot be sent to existent user " + existentUser.getLogin());
			return;
		}
		
		Date now = new Date();
		Invitation invitation = new Invitation();
		invitation.setEmail(email);
		invitation.setName(name);
		invitation.setDate(now);
		invitation.setLeague(league);
		invitation.setAdminLogin(adminLogin);
		
		int atIndex = invitation.getEmail().indexOf("@");
		if (atIndex < 0) {
			logger.info("Invitation not sent, invalid email address");
			return;
		}
		String fakeLogin = invitation.getEmail().substring(0, atIndex);
		String registrationUrl = this.baseUrl + "/invitation/" + league.getId() + "/" + fakeLogin;
			
		this.invitationRepository.save(invitation);
		this.emailService.sendInvitations(league.getName(), invitation, registrationUrl);
		logger.debug("Created invitation for " + name + ", " + email);
	}

}
