package org.jgayoso.ncomplo.business.services;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jgayoso.ncomplo.business.entities.Bet;
import org.jgayoso.ncomplo.business.entities.BetType;
import org.jgayoso.ncomplo.business.entities.Competition;
import org.jgayoso.ncomplo.business.entities.Game;
import org.jgayoso.ncomplo.business.entities.League;
import org.jgayoso.ncomplo.business.entities.LeagueGame;
import org.jgayoso.ncomplo.business.entities.User;
import org.jgayoso.ncomplo.business.entities.repositories.BetRepository;
import org.jgayoso.ncomplo.business.entities.repositories.BetTypeRepository;
import org.jgayoso.ncomplo.business.entities.repositories.CompetitionRepository;
import org.jgayoso.ncomplo.business.entities.repositories.GameRepository;
import org.jgayoso.ncomplo.business.entities.repositories.LeagueRepository;
import org.jgayoso.ncomplo.business.util.I18nNamedEntityComparator;
import org.jgayoso.ncomplo.business.util.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class LeagueService {
    
    
    @Autowired
    private LeagueRepository leagueRepository;
    
    @Autowired
    private CompetitionRepository competitionRepository;
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private BetTypeRepository betTypeRepository;
    
    @Autowired
    private BetRepository betRepository;
 
    
    
    
    public LeagueService() {
        super();
    }
    
    
    @Transactional
    public League find(final Integer id) {
        return this.leagueRepository.findOne(id);
    }
    
    
    @Transactional
    public List<League> findAll(final Locale locale) {
        final List<League> leagues = IterableUtils.toList(this.leagueRepository.findAll());
        Collections.sort(leagues, new I18nNamedEntityComparator(locale));
        return leagues;
    }

    
    
    @Transactional
    public League save(
            final Integer id,
            final Integer competitionId,
            final String name, 
            final Map<String,String> namesByLang,
            final String adminEmail,
            final boolean active,
            final Map<Integer,Integer> betTypesByGame) {

        final Competition competition = 
                this.competitionRepository.findOne(competitionId);
        
        final League league =
                (id == null? new League() : this.leagueRepository.findOne(id));

        league.setCompetition(competition);
        league.setName(name);
        league.getNamesByLang().clear();
        league.getNamesByLang().putAll(namesByLang);
        league.setAdminEmail(adminEmail);
        league.setActive(active);
        league.getLeagueGames().clear();
        
        for (final Map.Entry<Integer,Integer> betTypesByGameEntry : betTypesByGame.entrySet()) {
            
            final Integer gameId = betTypesByGameEntry.getKey();
            final Integer betTypeId = betTypesByGameEntry.getValue();
            
            final Game game = this.gameRepository.findOne(gameId);
            final BetType betType = this.betTypeRepository.findOne(betTypeId);
            
            final LeagueGame leagueGame = new LeagueGame();
            leagueGame.setBetType(betType);
            leagueGame.setGame(game);
            leagueGame.setLeague(league);
            
            league.getLeagueGames().put(game,leagueGame);
            
        }
        
        if (id == null) {
            return this.leagueRepository.save(league);
        }
        return league;
        
    }
    

    
    @Transactional
    public void delete(final Integer leagueId) {
        this.leagueRepository.delete(leagueId);
    }

    
    

    
    @Transactional
    public void recomputeScores(final Integer leagueId) {
        
        final League league = this.leagueRepository.findOne(leagueId);
        
        for (final User participant : league.getParticipants()) {
            final List<Bet> bets = 
                    this.betRepository.findByLeagueIdAndUserLogin(league.getId(), participant.getLogin());
            for (final Bet bet : bets) {
                bet.evaluate();
            }
        }
        
    }
    
    
}
