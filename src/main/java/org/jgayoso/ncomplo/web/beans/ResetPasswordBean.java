package org.jgayoso.ncomplo.web.beans;

import java.io.Serializable;

public class ResetPasswordBean implements Serializable {
    
    private static final long serialVersionUID = 1984212722109860979L;
    private final String login;
    private final String email;
    
    public ResetPasswordBean(String login, String email) {
        this.login = login;
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

}
