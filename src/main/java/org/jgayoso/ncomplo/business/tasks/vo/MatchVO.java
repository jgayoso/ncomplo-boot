package org.jgayoso.ncomplo.business.tasks.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchVO {

	private String status;
	private String time;
	private TeamVO home_team;
	private TeamVO away_team;

	public String getStatus() {
		return this.status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public String getTime() {
		return this.time;
	}

	public void setTime(final String time) {
		this.time = time;
	}

	public TeamVO getHome_team() {
		return this.home_team;
	}

	public void setHome_team(final TeamVO home_team) {
		this.home_team = home_team;
	}

	public TeamVO getAway_team() {
		return this.away_team;
	}

	public void setAway_team(final TeamVO away_team) {
		this.away_team = away_team;
	}

	@Override
	public String toString() {
		return "MatchVO [status=" + this.status + ", time=" + this.time + ", home_team=" + this.home_team
				+ ", away_team=" + this.away_team + "]";
	}

}
