package org.jgayoso.ncomplo.business.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jgayoso.ncomplo.business.entities.Bet;
import org.jgayoso.ncomplo.business.entities.Bet.BetComparator;
import org.jgayoso.ncomplo.business.entities.Game;
import org.jgayoso.ncomplo.business.entities.GameSide;
import org.jgayoso.ncomplo.business.entities.League;
import org.jgayoso.ncomplo.business.entities.LeagueGame;
import org.jgayoso.ncomplo.business.entities.User;
import org.jgayoso.ncomplo.business.entities.repositories.BetRepository;
import org.jgayoso.ncomplo.business.entities.repositories.GameRepository;
import org.jgayoso.ncomplo.business.entities.repositories.GameSideRepository;
import org.jgayoso.ncomplo.business.entities.repositories.LeagueGameRepository;
import org.jgayoso.ncomplo.business.entities.repositories.LeagueRepository;
import org.jgayoso.ncomplo.business.entities.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class BetService {
    
    
    @Autowired
    private LeagueRepository leagueRepository;
    
    @Autowired
    private LeagueGameRepository leagueGameRepository;
    
    @Autowired
    private LeagueService leagueService;
    
    @Autowired
    private BetRepository betRepository;
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private GameService gameService;
    
    @Autowired
    private GameSideRepository gameSideRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
 
    private final String groupsFirstColumnName = "E";
    private final String[] secondRoundColumnNames = {"EW", "EX"};
    private final String[] quarterFinalsColumnNames = {"FD", "FE"};
    private final String[] semisColumnNames = {"FK", "FL"};
    private final String[] finalColumnNames = {"FR", "FS"};
    
    public BetService() {
        super();
    }
    
    
    @Transactional
    public Bet find(final Integer id) {
        return this.betRepository.findOne(id);
    }
    
    
    @Transactional
    public List<Bet> findByLeagueIdAndUserLogin(
            final Integer leagueId, final String login, final Locale locale) {
        final List<Bet> bets = 
                this.betRepository.findByLeagueIdAndUserLogin(leagueId, login);
        Collections.sort(bets, new BetComparator(locale));
        return bets;
    }
    
    @Transactional
    public void processBetsFile(final File betsFile, final String login, final Integer leagueId) {
        FileInputStream fis = null;
        XSSFWorkbook book = null;
        
        final User participant = this.userService.find(login);
        final League league = this.leagueService.find(leagueId);
        final Collection<LeagueGame> leagueGames = league.getLeagueGames().values();
        final List<LeagueGame> sortedLeagueGames = new ArrayList<LeagueGame>();
        
        try {
            
            fis = new FileInputStream(betsFile);
            book = new XSSFWorkbook(fis);
            final XSSFSheet sheet = book.getSheetAt(3);

            int matchNumber = 1;
            for (int rowIndex=10; rowIndex<46; rowIndex++) {
                this.processGroupsGameBet(sheet, 10, matchNumber, leagueId);
                matchNumber++;
            }
            return;
        } catch (final IOException e) {
            //Throw exception
            return;
        } finally {
            try {
                if (book != null) {book.close(); }
                if (fis != null) { fis.close(); }
            } catch (final Exception e) { 
                // Nothing to do
            }
        }
    }
    
    private void processGroupsGameBet(final XSSFSheet sheet, final int rowIndex, final int matchNumber,
            final Integer leagueId) {
        
        final CellReference cellReference = new CellReference(this.groupsFirstColumnName + rowIndex);
        final Row row = sheet.getRow(cellReference.getRow());
        final Cell homeTeamCell = row.getCell(cellReference.getCol());
        final Cell homeResultCell = row.getCell(cellReference.getCol() + 1);
        final Cell awayResultCell = row.getCell(cellReference.getCol() + 2);
        final Cell awayTeamCell = row.getCell(cellReference.getCol() + 3);
        
        final String homeTeamName = homeTeamCell.getStringCellValue();
        final String awayTeamName = awayTeamCell.getStringCellValue();
        final int homeResult = Double.valueOf(homeResultCell.getNumericCellValue()).intValue();
        final int awayResult = Double.valueOf(awayResultCell.getNumericCellValue()).intValue();
        
        
        
        
    }
    
    

    
    
    @Transactional
    public Bet save(
            final Integer id,
            final Integer leagueId,
            final String login,
            final Integer gameId,
            final Integer gameSideAId,
            final Integer gameSideBId,
            final Integer scoreA,
            final Integer scoreB) {

        final League league = 
                this.leagueRepository.findOne(leagueId);
        final User user = 
                this.userRepository.findOne(login);
        final Game game = 
                this.gameRepository.findOne(gameId);
        
        final GameSide gameSideA = 
                (gameSideAId == null? null : this.gameSideRepository.findOne(gameSideAId));
        final GameSide gameSideB = 
                (gameSideBId == null? null : this.gameSideRepository.findOne(gameSideBId));
        
        final Bet bet =
                (id == null? new Bet() : this.betRepository.findOne(id));

        bet.setLeague(league);
        bet.setGame(game);
        bet.setUser(user);
        bet.setGameSideA(gameSideA);
        bet.setGameSideB(gameSideB);
        bet.setScoreA(scoreA);
        bet.setScoreB(scoreB);
        
        if (id == null) {
            return this.betRepository.save(bet);
        }
        return bet;
        
    }
    

    
    @Transactional
    public void delete(final Integer betId) {
        this.betRepository.delete(betId);
    }

    
    
    
}
