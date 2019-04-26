package com.weapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weapp.entity.file.TFile;
import com.weapp.repository.FileRepository;
@Service
public class FileService {
	@Autowired
	private FileRepository fileDao;
	
	/**
	 * 保存文件
	 * @param file
	 * @return
	 */
	public String save(TFile file){
		fileDao.save(file);
		return file.get_id();
	}
	
	
	public TFile getFile(String id){
		List<TFile> files = fileDao.findByImgId(id);
		if(files!=null&&files.size()>0){
			return files.get(0);
		}
		return null;
	}
	
}
