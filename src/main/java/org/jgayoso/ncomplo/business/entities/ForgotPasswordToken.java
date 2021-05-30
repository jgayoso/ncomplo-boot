package org.jgayoso.ncomplo.business.entities;

import javax.persistence.*;

@Entity
@Table(name = "FORGOT_PASSWORD_TOKEN")
public class ForgotPasswordToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "LOGIN")
    private String login;

    @Column(name = "TOKEN")
    private String token;

    public ForgotPasswordToken() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
