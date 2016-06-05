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

	@Column(name = "EMAIL", nullable = false)
	private String email;
	
	@Column(name = "NAME", nullable = false)
	private String name;
	
	@Column(name = "ADMIN_LOGIN", nullable = false)
	private String adminLogin;

	@Column(name = "DATE", nullable = true)
	private Date date;
	
	public Invitation() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public League getLeague() {
		return league;
	}

	public void setLeague(League league) {
		this.league = league;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getAdminLogin() {
		return adminLogin;
	}

	public void setAdminLogin(String adminLogin) {
		this.adminLogin = adminLogin;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
