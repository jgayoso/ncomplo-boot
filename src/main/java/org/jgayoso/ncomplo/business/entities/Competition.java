package org.jgayoso.ncomplo.business.entities;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.jgayoso.ncomplo.business.util.I18nUtils;


@Entity
@Table(name="COMPETITION")
public class Competition implements I18nNamedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    
    @Column(name="NAME",nullable=false,length=200)
    private String name;
    
    
    @ElementCollection(fetch=FetchType.LAZY,targetClass=java.lang.String.class)
    @CollectionTable(name="COMPETITION_NAME_I18N",joinColumns=@JoinColumn(name="COMPETITION_ID"))
    @MapKeyColumn(name="LANG",nullable=false,length=20)
    @Column(name="NAME", nullable=false,length=200)
    private final Map<String,String> namesByLang = new LinkedHashMap<>();

    
    @Column(name="ACTIVE",nullable=false)
    private boolean active = true;

	@Column(name = "UPDATER_URI", nullable = false)
	private String updaterUri;

    
    @OneToMany(cascade=CascadeType.ALL,orphanRemoval=true,mappedBy="competition")
    private final Set<BetType> betTypes = new LinkedHashSet<>();

    
    @OneToMany(cascade=CascadeType.ALL,orphanRemoval=true,mappedBy="competition")
    private final Set<Round> rounds = new LinkedHashSet<>();

    
    @OneToMany(cascade=CascadeType.ALL,orphanRemoval=true,mappedBy="competition")
    private final Set<GameSide> gameSides = new LinkedHashSet<>();

    
    @OneToMany(cascade=CascadeType.ALL,orphanRemoval=true,mappedBy="competition")
    private final Set<Game> games = new LinkedHashSet<>();
    
    
    
    
    public Competition() {
        super();
    }


    @Override
    public Map<String, String> getNamesByLang() {
        return this.namesByLang;
    }


    public Integer getId() {
        return this.id;
    }


    @Override
    public String getName(final Locale locale) {
        return I18nUtils.getTextForLocale(locale, this.namesByLang, this.name);
    }


    public boolean isActive() {
        return this.active;
    }


    public void setActive(final boolean active) {
        this.active = active;
    }
    

    public Set<BetType> getBetTypes() {
        return this.betTypes;
    }
    

    public Set<Round> getRounds() {
        return this.rounds;
    }
    

    public Set<GameSide> getGameSides() {
        return this.gameSides;
    }
    

    public Set<Game> getGames() {
        return this.games;
    }


    @Override
    public String getName() {
        return this.name;
    }


    public void setName(final String name) {
        this.name = name;
    }

	public String getUpdaterUri() {
		return this.updaterUri;
	}

	public void setUpdaterUri(final String updaterUri) {
		this.updaterUri = updaterUri;
	}
    
}
