package org.jgayoso.ncomplo.web.admin.beans;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class InvitationBean implements Serializable {
	
	private static final long serialVersionUID = -4659300739087101820L;

	@NotNull
	private Integer leagueId;
	
	private String name;
	
	private String email;

	public InvitationBean() {
		super();
	}
	
	public Integer getLeagueId() {
		return this.leagueId;
	}

	public void setLeagueId(final Integer leagueId) {
		this.leagueId = leagueId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}
	
}
