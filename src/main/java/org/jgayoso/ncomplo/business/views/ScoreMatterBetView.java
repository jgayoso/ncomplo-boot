package org.jgayoso.ncomplo.business.views;

import java.io.Serializable;

public class ScoreMatterBetView implements Serializable {
	
	private static final long serialVersionUID = 8984540730086651878L;
	
	private final String login;
	private final Integer gameId; 
	private final Integer scoreA;
    private final Integer scoreB;
    
	public ScoreMatterBetView(String login, Integer gameId, Integer scoreA, Integer scoreB) {
		super();
		this.login = login;
		this.gameId = gameId;
		this.scoreA = scoreA;
		this.scoreB = scoreB;
	}

	public String getLogin() {
		return login;
	}

	public Integer getGameId() {
		return gameId;
	}

	public Integer getScoreA() {
		return scoreA;
	}

	public Integer getScoreB() {
		return scoreB;
	}

}
