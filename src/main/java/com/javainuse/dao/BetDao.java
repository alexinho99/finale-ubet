package com.javainuse.dao;

import com.javainuse.model.BetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetDao extends CrudRepository<BetEntity, Integer> {
    public List<BetEntity> getAllByUserId(Integer userId);

    public BetEntity getByMatchId(Integer matchId);
}
