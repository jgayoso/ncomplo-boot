package org.jgayoso.ncomplo.business.services;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
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
	private EmailService emailService;
	@Autowired
    private UserService userService;
	
	@Value("${ncomplo.server.url}")
	private String baseUrl;
	
	public InvitationService() {
		super();
	}
	
	public Invitation findById(final Integer invitationId) {
        return this.invitationRepository.findOne(invitationId);
    }
	
	public Invitation findByToken(final String token) {
        return this.invitationRepository.findByToken(token);
    }
	
	public List<Invitation> findByLeagueId(final Integer leagueId) {
		return this.invitationRepository.findByLeagueIdAndTokenIsNull(leagueId);
	}
	
	public Invitation findGroupByLeagueId(final Integer leagueId) {
		return this.invitationRepository.findByLeagueIdAndTokenIsNotNull(leagueId);
	}
	
	public Invitation findByLeagueIdAndEmail(final Integer leagueId, final String email) {
		return this.invitationRepository.findByLeagueIdAndEmail(leagueId, email);
	}
	
	@Transactional
	public void sendInvitations(final Integer leagueId, final String adminLogin, final String name, final String email, final Locale locale) {
		final League league = this.leagueService.find(leagueId);
		if (league == null) {
			logger.info("Trying to send an invitation for a non existent league " + leagueId);
			return;
		}
		
		final User user = this.userService.findByEmail(email);
		
		final Invitation existentInvitation = this.invitationRepository.findByLeagueIdAndEmail(leagueId, email);
        if (existentInvitation != null) {
            // send the invitation again
            final String registrationUrl = this.generateRegistrationUrl(existentInvitation, league.getId());
            this.emailService.sendInvitations(league.getName(), existentInvitation, registrationUrl, user, locale);
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
		
        final String registrationUrl = this.generateRegistrationUrl(inv, league.getId());
			
		this.emailService.sendInvitations(league.getName(), invitation, registrationUrl, user, locale);
		logger.debug("Created invitation for " + name + ", " + email);
	}
	
	@Transactional
	public void generateInvitationGroup(final Integer leagueId, final String adminLogin) {
		final League league = this.leagueService.find(leagueId);
		if (league == null) {
			logger.info("Trying to send an invitation for a non existent league " + leagueId);
			return;
		}
		
		final Invitation existingInvitation = this.findGroupByLeagueId(leagueId);
		if (existingInvitation != null) {
			return;
		}
		
		final String token = RandomStringUtils.randomAlphanumeric(5);
		
		final Date now = new Date();
		final Invitation invitation = new Invitation();
		invitation.setDate(now);
		invitation.setLeague(league);
		invitation.setAdminLogin(adminLogin);
		invitation.setToken(token);
		
		this.invitationRepository.save(invitation);
		logger.debug("Created group invitation for league " + leagueId+ " with token " + token);
		
	}
	
	private String generateRegistrationUrl(final Invitation invitation, final Integer leagueId) {
	    final int atIndex = invitation.getEmail().indexOf("@");
        if (atIndex < 0) {
            logger.info("Invitation not sent, invalid email address");
            return "";
        }
        final String fakeLogin = invitation.getEmail().substring(0, atIndex);
	    return this.baseUrl + "/invitation/" + invitation.getId() + "/" + leagueId + "/" + fakeLogin;
	}


}
