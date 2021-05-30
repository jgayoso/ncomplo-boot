package org.jgayoso.ncomplo.business.entities.repositories;

import org.jgayoso.ncomplo.business.entities.ForgotPasswordToken;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ForgotPasswordTokenRepository extends PagingAndSortingRepository<ForgotPasswordToken, Integer> {

    public ForgotPasswordToken findByToken(final String token);

    public ForgotPasswordToken findByEmail(final String email);

    public ForgotPasswordToken findByLogin(final String login);

}
