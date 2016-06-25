package org.jgayoso.ncomplo.business.views;

import java.io.Serializable;
import java.util.List;

public class TodayEventsView implements Serializable {

	private static final long serialVersionUID = -4219625700521193270L;

	private final List<TodayRoundGamesAndBetsView> roundsInformation;

	public TodayEventsView(List<TodayRoundGamesAndBetsView> roundsInformation) {
		super();
		this.roundsInformation = roundsInformation;
	}

	public List<TodayRoundGamesAndBetsView> getRoundsInformation() {
		return roundsInformation;
	}

}
