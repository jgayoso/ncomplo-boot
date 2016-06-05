package org.jgayoso.ncomplo.business.entities.repositories;

import java.util.List;

import org.jgayoso.ncomplo.business.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends PagingAndSortingRepository<User,String> {

    @Query("from User u where u.admin = true")
    public List<User> findAllAdmin();
    
    public User findByEmail(String email);
    
}
    