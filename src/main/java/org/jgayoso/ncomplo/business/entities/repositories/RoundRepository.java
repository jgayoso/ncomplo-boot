package org.jgayoso.ncomplo.business.entities.repositories;

import java.util.List;

import org.jgayoso.ncomplo.business.entities.Round;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoundRepository 
        extends PagingAndSortingRepository<Round,Integer> {
    
    public List<Round> findByCompetitionId(final Integer competitionId);
    
}
    