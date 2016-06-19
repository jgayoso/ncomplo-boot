package org.jgayoso.ncomplo.business.views;

import java.io.Serializable;

public class UserBetView implements Serializable {
	
	private static final long serialVersionUID = -4549917617599525445L;

	private String login;
	private Integer gameId; 
	private Integer scoreA;
    private Integer scoreB;
    private Boolean scoreMatter;
    
	public UserBetView(String login, Integer gameId, Integer scoreA, Integer scoreB, Boolean scoreMatter) {
		super();
		this.login = login;
		this.gameId = gameId;
		this.scoreA = scoreA;
		this.scoreB = scoreB;
		this.scoreMatter = scoreMatter;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
	public Integer getScoreA() {
		return scoreA;
	}
	public void setScoreA(Integer scoreA) {
		this.scoreA = scoreA;
	}
	public Integer getScoreB() {
		return scoreB;
	}
	public void setScoreB(Integer scoreB) {
		this.scoreB = scoreB;
	}
	public Boolean isScoreMatter() {
		return scoreMatter;
	}
	public void setScoreMatter(Boolean scoreMatters) {
		this.scoreMatter = scoreMatters;
	}
    
}
