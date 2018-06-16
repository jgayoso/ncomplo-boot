package org.jgayoso.ncomplo.exceptions;

public class LeagueClosedException extends Exception {
	
	private static final long serialVersionUID = 2673344909881114275L;
	
	public LeagueClosedException(final Integer leagueId) {
		super("Closed league " + leagueId + " exception");
	}
	
}
