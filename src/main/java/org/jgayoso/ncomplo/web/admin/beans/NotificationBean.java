package org.jgayoso.ncomplo.web.admin.beans;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class NotificationBean implements Serializable {

	private static final long serialVersionUID = 5206428672026881880L;
	
	@NotNull
	private Integer leagueId;

	@NotNull
	private String subject;

	@NotNull
	private String text;

	public NotificationBean() {
		super();
	}
	
	public NotificationBean(final Integer leagueId) {
		super();
		this.leagueId = leagueId;
	}

	public Integer getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(Integer leagueId) {
		this.leagueId = leagueId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
