package com.weapp.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.weapp.entity.file.TFile;
/**
 * appkey管理操作
 * @author zk
 *
 */
public interface FileRepository extends MongoRepository<TFile, String> {
	
	List<TFile> findByImgId(String imgId);
}
