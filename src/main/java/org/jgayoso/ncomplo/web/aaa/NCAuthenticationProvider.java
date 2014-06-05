package org.jgayoso.ncomplo.web.aaa;

import org.apache.log4j.Logger;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class NCAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = Logger.getLogger(NCAuthenticationProvider.class);

    @Autowired
    private PasswordEncryptor passwordEncryptor;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        if (logger.isInfoEnabled()) {
            logger.info("Trying to authenticate user " + authentication.getName());
        }
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(authentication.getName());
        if (userDetails != null) {
            try {
                if (this.passwordEncryptor.checkPassword(authentication.getCredentials().toString(), userDetails.getPassword())) {
                    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                }
            } catch (EncryptionOperationNotPossibleException e) {
                if (logger.isInfoEnabled()) {
                    logger.info("Authentication failed for user " + authentication.getName());
                }
            }
        }

        throw new BadCredentialsException("Bad credentials for user " + authentication.getName());
    }

    @Override
    public boolean supports(Class<?> authentication) {
    	return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
