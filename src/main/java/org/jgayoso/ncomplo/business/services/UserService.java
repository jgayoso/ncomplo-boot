package org.jgayoso.ncomplo.business.services;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.RandomStringUtils;
import org.jasypt.util.password.PasswordEncryptor;
import org.jgayoso.ncomplo.business.entities.League;
import org.jgayoso.ncomplo.business.entities.User;
import org.jgayoso.ncomplo.business.entities.User.UserComparator;
import org.jgayoso.ncomplo.business.entities.repositories.LeagueRepository;
import org.jgayoso.ncomplo.business.entities.repositories.UserRepository;
import org.jgayoso.ncomplo.business.util.IterableUtils;
import org.jgayoso.ncomplo.exceptions.InternalErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LeagueRepository leagueRepository;

    @Autowired
    private PasswordEncryptor passwordEncryptor;

    @Autowired
    private EmailService emailService;
    

    
    
    public UserService() {
        super();
    }
    
    
    @Transactional
    public User find(final String login) {
        return this.userRepository.findOne(login);
    }
    
    
    @Transactional
    public List<User> findAll(final Locale locale) {
        final List<User> users = 
                IterableUtils.toList(this.userRepository.findAll());
        Collections.sort(users, new UserComparator(locale));
        return users;
    }
    
    public long countUsers() {
    	return this.userRepository.count();
    }

    
    
    @Transactional
    public User save(
            final String login,
            final String name,
            final String email,
            final boolean admin,
            final boolean active,
            final List<Integer> leagueIds) {
        
        final boolean userExists = this.userRepository.exists(login);
        
        final User user =
                (!userExists? new User() : this.userRepository.findOne(login));
        
        user.setLogin(login);
        user.setName(name);
        user.setEmail(email);
        user.setAdmin(admin);
        user.setActive(active);

        for (final League league : user.getLeagues()) {
            league.getParticipants().remove(user);
        }
        user.getLeagues().clear();
        for (final Integer leagueId : leagueIds) {
            final League league = this.leagueRepository.findOne(leagueId);
            user.getLeagues().add(league);
            league.getParticipants().add(user);
        }
        
        if (!userExists) {
            return this.userRepository.save(user);
        }
        
        return user;
        
    }

    
    
    @Transactional
    public String resetPassword(final String login, final boolean sendEmail) {
        
        final String newPassword = 
                RandomStringUtils.randomAlphanumeric(10);
        final String hashedNewPassword = 
                this.passwordEncryptor.encryptPassword(newPassword);

        final User user = this.userRepository.findOne(login);
        user.setPassword(hashedNewPassword);
    
        if (sendEmail) {
            this.emailService.sendNewPassword(login, newPassword);
        }
        
        return newPassword;
        
    }

    
    
    @Transactional
    public User changePassword(final String login, 
            final String oldPassword, final String newPassword) {
        
        final User user =
                this.userRepository.findOne(login);
        
        final String oldHashedPassword = user.getPassword();
        
        if (!this.passwordEncryptor.checkPassword(oldPassword, oldHashedPassword)) {
            throw new InternalErrorException("Old password does not match!");
        }
        
        final String hashedNewPassword = 
                this.passwordEncryptor.encryptPassword(newPassword);
        user.setPassword(hashedNewPassword);
        
        return user;
        
    }
    

    
    
    @Transactional
    public void delete(final String login) {
        this.userRepository.delete(login);
    }

    
    
}
