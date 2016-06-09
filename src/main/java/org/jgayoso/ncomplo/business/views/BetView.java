package org.jgayoso.ncomplo.business.views;

import java.io.Serializable;

public class BetView implements Serializable {

	private static final long serialVersionUID = 6519689142053375922L;

	private Integer id;
    
    private Integer gameId; 
    
    private Integer betTypeId; 

    private Integer gameSideAId;

    private Integer gameSideBId;
    
    private Integer scoreA;
    
    private Integer scoreB;
    
    
    public BetView() {
    	super();
    }
    
    public Integer getId() {
        return this.id;
    }


    public void setId(final Integer id) {
        this.id = id;
    }


    public Integer getGameId() {
        return this.gameId;
    }


    public void setGameId(final Integer gameId) {
        this.gameId = gameId;
    }


    public Integer getBetTypeId() {
        return this.betTypeId;
    }


    public void setBetTypeId(final Integer betTypeId) {
        this.betTypeId = betTypeId;
    }


    public Integer getGameSideAId() {
        return this.gameSideAId;
    }


    public void setGameSideAId(final Integer gameSideAId) {
        this.gameSideAId = gameSideAId;
    }


    public Integer getGameSideBId() {
        return this.gameSideBId;
    }


    public void setGameSideBId(final Integer gameSideBId) {
        this.gameSideBId = gameSideBId;
    }


    public Integer getScoreA() {
        return this.scoreA;
    }


    public void setScoreA(final Integer scoreA) {
        this.scoreA = scoreA;
    }


    public Integer getScoreB() {
        return this.scoreB;
    }

    
    public void setScoreB(final Integer scoreB) {
        this.scoreB = scoreB;
    }
}
