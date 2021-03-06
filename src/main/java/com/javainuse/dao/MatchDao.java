package com.javainuse.dao;

import com.javainuse.model.DAOUser;
import com.javainuse.model.MatchesEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MatchDao extends CrudRepository<MatchesEntity, Integer> {
    public MatchesEntity findByHref(Integer href);

    @Transactional
    @Modifying
    @Query("update MatchesEntity m set m.finished=true")
    public void setAllAsFinished();
}
