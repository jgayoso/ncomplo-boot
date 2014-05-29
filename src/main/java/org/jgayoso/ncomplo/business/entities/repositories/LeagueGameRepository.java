package org.jgayoso.ncomplo.business.entities.repositories;

import org.jgayoso.ncomplo.business.entities.LeagueGame;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LeagueGameRepository 
        extends PagingAndSortingRepository<LeagueGame,Integer> {
    
    // No methods to add
    
}
    