package org.jgayoso.ncomplo.business.entities.repositories;

import java.util.List;

import org.jgayoso.ncomplo.business.entities.Competition;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CompetitionRepository 
        extends PagingAndSortingRepository<Competition,Integer> {

	public List<Competition> findByActiveIsTrueAndUpdaterUriIsNotNull();
    
}
    