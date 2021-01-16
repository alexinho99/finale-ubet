package com.javainuse.dao;

import com.javainuse.model.SoccerSiteEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoccerSiteDao extends CrudRepository<SoccerSiteEntity, Integer> {
    SoccerSiteEntity findTopByOrderByIdDesc();
}
