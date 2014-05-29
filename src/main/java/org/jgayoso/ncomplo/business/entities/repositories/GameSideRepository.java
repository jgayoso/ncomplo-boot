package org.jgayoso.ncomplo.business.entities.repositories;

import java.util.List;

import org.jgayoso.ncomplo.business.entities.GameSide;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GameSideRepository 
        extends PagingAndSortingRepository<GameSide,Integer> {
    
    public List<GameSide> findByCompetitionId(final Integer competitionId);
    
}
    