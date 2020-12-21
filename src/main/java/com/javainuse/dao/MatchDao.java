package com.javainuse.dao;

import com.javainuse.model.DAOUser;
import com.javainuse.model.MatchesEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchDao extends CrudRepository<MatchesEntity, Integer> {

}
