package org.jgayoso.ncomplo.business.services;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.jgayoso.ncomplo.business.entities.Invitation;
import org.jgayoso.ncomplo.business.entities.League;
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
	private EmailService emailService;
	
	@Value("${ncomplo.server.url}")
	private String baseUrl;
	
	public InvitationService() {
		super();
	}
	
	public Invitation findById(final Integer invitationId) {
        return this.invitationRepository.findOne(invitationId);
    }
	
	public List<Invitation> findByLeagueId(final Integer leagueId) {
		return this.invitationRepository.findByLeagueId(leagueId);
	}
	
	public Invitation findByLeagueIdAndEmail(final Integer leagueId, final String email) {
		return this.invitationRepository.findByLeagueIdAndEmail(leagueId, email);
	}
	
	@Transactional
	public void sendInvitations(final Integer leagueId, final String adminLogin, final String name, final String email) {
		final League league = this.leagueService.find(leagueId);
		if (league == null) {
			logger.info("Trying to send an invitation for a non existent league " + leagueId);
			return;
		}
		
		Invitation existentInvitation = this.invitationRepository.findByLeagueIdAndEmail(leagueId, email);
        if (existentInvitation != null) {
            // send the invitation again
            final String registrationUrl = generateRegistrationUrl(existentInvitation, league.getId());
            this.emailService.sendInvitations(league.getName(), existentInvitation, registrationUrl);
            logger.debug("Created invitation for " + name + ", " + email);
            return;
        }
        
		final Date now = new Date();
		final Invitation invitation = new Invitation();
		invitation.setEmail(email);
		invitation.setName(name);
		invitation.setDate(now);
		invitation.setLeague(league);
		invitation.setAdminLogin(adminLogin);
		
		final Invitation inv = this.invitationRepository.save(invitation);
		
        final String registrationUrl = generateRegistrationUrl(inv, league.getId());
			
		this.emailService.sendInvitations(league.getName(), invitation, registrationUrl);
		logger.debug("Created invitation for " + name + ", " + email);
	}
	
	private String generateRegistrationUrl(Invitation invitation, Integer leagueId) {
	    final int atIndex = invitation.getEmail().indexOf("@");
        if (atIndex < 0) {
            logger.info("Invitation not sent, invalid email address");
            return "";
        }
        final String fakeLogin = invitation.getEmail().substring(0, atIndex);
	    return this.baseUrl + "/invitation/" + invitation.getId() + "/" + leagueId + "/" + fakeLogin;
	}

}
