package org.jgayoso.ncomplo.business.tasks.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamVO {
	
	private String country;
	private String code;
	private Integer goals;
	
	public String getCountry() {
		return this.country;
	}
	public void setCountry(final String country) {
		this.country = country;
	}
	public String getCode() {
		return this.code;
	}
	public void setCode(final String code) {
		this.code = code;
	}
	public Integer getGoals() {
		return this.goals;
	}
	public void setGoals(final Integer goals) {
		this.goals = goals;
	}
	@Override
	public String toString() {
		return "TeamVO [country=" + this.country + ", code=" + this.code + ", goals=" + this.goals + "]";
	}
	
}
