package com.weapp.redis;

public class Rank {
	private long rank;
	private Double score;
	private String member;
	
	public Rank(long rank, Double score,String member) {
		super();
		this.rank = rank;
		this.score = score;
		this.member=member;
	}
	public long getRank() {
		return rank;
	}
	public Double getScore() {
		return score;
	}
	public String getMember() {
		return member;
	}
	
	
	
	
	

}
