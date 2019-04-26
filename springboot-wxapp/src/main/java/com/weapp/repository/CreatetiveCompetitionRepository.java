package com.weapp.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.weapp.entity.conf.TCreativeCompetition;
public interface CreatetiveCompetitionRepository extends MongoRepository<TCreativeCompetition, String> {
	TCreativeCompetition findByStage(String stage);
	
	List<TCreativeCompetition> findAll();
}
