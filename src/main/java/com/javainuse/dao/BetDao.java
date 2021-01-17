package com.javainuse.dao;

import com.javainuse.model.BetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetDao extends CrudRepository<BetEntity, Integer> {
    public List<BetEntity> getAllByUserId(Integer userId);

    public BetEntity getByMatchId(Integer matchId);

    @Query(value = "SELECT * FROM bets WHERE user_id = :userId AND match_id IN (SELECT id FROM matches WHERE finished = :finished)", nativeQuery = true)
    List<BetEntity> getAllWithMatchFinished(@Param("userId") Integer userId, @Param("finished") Boolean finished);
}
