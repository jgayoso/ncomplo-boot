package org.jgayoso.ncomplo.business.entities.repositories;

import java.util.List;

import org.jgayoso.ncomplo.business.entities.League;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LeagueRepository 
        extends PagingAndSortingRepository<League,Integer> {
    
    public List<League> findByCompetitionId(final Integer competitionId);
    
}
    