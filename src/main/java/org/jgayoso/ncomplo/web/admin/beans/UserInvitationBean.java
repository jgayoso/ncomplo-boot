package org.jgayoso.ncomplo.web.admin.beans;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class UserInvitationBean implements Serializable {
	
	private static final long serialVersionUID = 8123975853250730018L;

	@NotNull
	private Integer invitationId;
	
	@NotNull
    private String login;
    
    @NotNull
    private String name;
    
    private String emailId;
    
    @NotNull
    private String email;
    
    @NotNull
    private String password;
    
    @NotNull
    private String password2;
    
    public UserInvitationBean() {
    	super();
    }

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public Integer getInvitationId() {
		return invitationId;
	}

	public void setInvitationId(Integer invitationId) {
		this.invitationId = invitationId;
	}
    
}
