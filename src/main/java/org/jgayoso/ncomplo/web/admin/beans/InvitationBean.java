package org.jgayoso.ncomplo.web.admin.beans;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class InvitationBean implements Serializable {
	
	private static final long serialVersionUID = -4659300739087101820L;

	@NotNull
	private Integer leagueId;
	
	@NotNull
	private String name;
	
	@NotNull
	private String email;

	public InvitationBean() {
		super();
	}
	
	public Integer getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(Integer leagueId) {
		this.leagueId = leagueId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
