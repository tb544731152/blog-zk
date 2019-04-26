package com.weapp.entity.vm;


import com.weapp.entity.wxarticle.Article;

public class ArticleResult{
	private Article art;
	private String[] imgsList = {};
	public ArticleResult(Article art) {
		this.art = art;
		if(!art.getImgs().isEmpty()){
			imgsList = art.getImgs().split(",");
			for(int i = 0 ; i< imgsList.length ; i++){
				imgsList[i] = "http://img.zyzsbj.cn/wxappservice/api/file/get/image/" + imgsList[i];
			}
		}
	}
	public Article getArt() {
		return art;
	}
	public void setArt(Article art) {
		this.art = art;
	}
	public String[] getImgsList() {
		return imgsList;
	}
	public void setImgsList(String[] imgsList) {
		this.imgsList = imgsList;
	}

}
