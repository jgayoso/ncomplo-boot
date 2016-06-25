package org.jgayoso.ncomplo.business.views;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.jgayoso.ncomplo.business.entities.Game;
import org.jgayoso.ncomplo.business.entities.GameSide;
import org.jgayoso.ncomplo.business.entities.Round;

public class TodayRoundGamesAndBetsView implements Serializable, Comparable<TodayRoundGamesAndBetsView> {

	private static final long serialVersionUID = -8509945906238964450L;
	
	private final Round round;
	private final List<Game> games;
	private int maxSideMattersBets;
	private final boolean scoreMatters;

	private final Map<String, Map<Integer, ScoreMatterBetView>> scoreMatterBets;
	private final Map<String, List<GameSide>> sideMatterBetsWinners;

	public TodayRoundGamesAndBetsView(final Round round, List<Game> games,
			Map<String, Map<Integer, ScoreMatterBetView>> scoreMatterBets,
			Map<String, List<GameSide>> sideMatterBetsWinners,
			final boolean scoreMatters) {
		super();
		this.round = round;
		this.games = games;
		this.maxSideMattersBets = 0;
		this.scoreMatterBets = scoreMatterBets;
		this.sideMatterBetsWinners = sideMatterBetsWinners;
		this.scoreMatters = scoreMatters;
	}

	public Round getRound() {
		return this.round;
	}
	
	public List<Game> getGames() {
		return games;
	}

	public int getMaxSideMattersBets() {
		return maxSideMattersBets;
	}

	public Map<String, Map<Integer, ScoreMatterBetView>> getScoreMatterBets() {
		return scoreMatterBets;
	}

	public Map<String, List<GameSide>> getSideMatterBetsWinners() {
		return sideMatterBetsWinners;
	}

	public void setMaxSideMattersBets(int maxSideMattersBets) {
		this.maxSideMattersBets = maxSideMattersBets;
	}

	public boolean isScoreMatters() {
		return this.scoreMatters;
	}

	@Override
	public int compareTo(TodayRoundGamesAndBetsView o) {
		return this.getRound().compareTo(o.getRound());
	}


}
