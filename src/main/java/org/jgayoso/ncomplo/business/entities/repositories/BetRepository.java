package org.jgayoso.ncomplo.business.entities.repositories;

import java.util.Collection;
import java.util.List;

import org.jgayoso.ncomplo.business.entities.Bet;
import org.jgayoso.ncomplo.business.entities.Game;
import org.jgayoso.ncomplo.business.entities.Round;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface BetRepository 
        extends PagingAndSortingRepository<Bet,Integer> {
    
    public List<Bet> findByLeagueIdAndUserLogin(final Integer leagueId, final String login);
    
	public List<Bet> findByLeagueIdAndUserLoginAndGameIn(final Integer leagueId, final String login,
			final Collection<Game> games);

    public List<Bet> findByScoreMatterTrueAndLeagueIdAndGameIn(final Integer leagueId, final Collection<Game> games);
    
    public List<Bet> findBySidesMatterTrueAndLeagueIdAndAndGameRound(final Integer leagueId, final Round round);
    
}
    