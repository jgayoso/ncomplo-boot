package org.jgayoso.ncomplo.business.entities.repositories;

import java.util.Date;
import java.util.List;

import org.jgayoso.ncomplo.business.entities.Competition;
import org.jgayoso.ncomplo.business.entities.Game;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GameRepository extends PagingAndSortingRepository<Game,Integer> {
    
    public List<Game> findByCompetitionId(final Integer competitionId);
    
    public List<Game> findByCompetitionAndDateBetween(Competition competition, Date initialDate, Date finalDate);
    
}
    