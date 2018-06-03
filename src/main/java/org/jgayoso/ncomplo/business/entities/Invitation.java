package org.jgayoso.ncomplo.business.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "INVITATION")
public class Invitation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "LEAGUE_ID", nullable = false)
	private League league;

	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "TOKEN")
	private String token;
	
	@Column(name = "ADMIN_LOGIN", nullable = false)
	private String adminLogin;

	@Column(name = "DATE", nullable = true)
	private Date date;
	
	public Invitation() {
		super();
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public League getLeague() {
		return this.league;
	}

	public void setLeague(final League league) {
		this.league = league;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}
	
	public String getAdminLogin() {
		return this.adminLogin;
	}

	public void setAdminLogin(final String adminLogin) {
		this.adminLogin = adminLogin;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(final String token) {
		this.token = token;
	}
	
	
}
