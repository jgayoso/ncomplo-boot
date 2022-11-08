package org.jgayoso.ncomplo.business.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.jgayoso.ncomplo.business.entities.repositories.LeagueRepository;
import org.jgayoso.ncomplo.business.entities.repositories.UserRepository;
import org.jgayoso.ncomplo.business.views.BetView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;



@Service
public class BetService {
    
    
    @Autowired
    private LeagueRepository leagueRepository;
    
    
    @Autowired
    private LeagueService leagueService;
    
    @Autowired
    private BetRepository betRepository;
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private GameSideRepository gameSideRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private final String groupsFirstColumnName = "E";
    private final String secondRoundColumnName = "AZ";
    private final String quarterFinalsColumnName = "BG";
    private final String semisColumnName = "BN";
    private final String finalColumnName = "BU";
    
    public BetService() {
        super();
    }
    
    
    public Bet find(final Integer id) {
        return this.betRepository.findOne(id);
    }
    
    public List<Bet> findByLeagueIdAndUserLogin(
            final Integer leagueId, final String login, final Locale locale) {
        final List<Bet> bets = 
                this.betRepository.findByLeagueIdAndUserLogin(leagueId, login);
        Collections.sort(bets, new BetComparator(locale));
        return bets;
    }
    
    @Transactional
    public void processBetsFile(final File betsFile, final String login, final Integer leagueId, final Locale locale) 
            throws IOException {
        FileInputStream fis = null;
        XSSFWorkbook book = null;
        
        final League league = this.leagueService.find(leagueId);
        final Collection<LeagueGame> leagueGames = league.getLeagueGames().values();
        
        final List<GameSide> gameSides = this.gameSideRepository.findByCompetitionId(league.getCompetition().getId());
        final Map<String, GameSide> gameSidesByName = new HashMap<>();
        for (final GameSide gameSide: gameSides) {
        	gameSidesByName.put(gameSide.getName(), gameSide);
        	for (final String name : gameSide.getNamesByLang().values()) {
        	    gameSidesByName.put(name, gameSide);
        	}
        }
        
        final List<Bet> bets =
                this.findByLeagueIdAndUserLogin(leagueId, login, locale);
        
        final Map<Integer, BetView> betViewssByGameId = new HashMap<Integer, BetView>();
        final Map<Integer, Game> gamesByOrder = new HashMap<Integer, Game>();
        final Map<Integer, Integer> betIdsByGameId = new HashMap<Integer, Integer>();
        
        if (!CollectionUtils.isEmpty(bets)) {
        	for (final Bet bet: bets) {
        		betIdsByGameId.put(bet.getGame().getId(), bet.getId());
        	}
        }
        
        
        for (final LeagueGame leagueGame : leagueGames) {
            final BetView betView = new BetView();
            final Game game = leagueGame.getGame();
            betView.setBetTypeId(leagueGame.getBetType().getId());
            betView.setGameId(game.getId());
            if (game.getGameSideA() != null) {
            	betView.setGameSideAId(game.getGameSideA().getId());
            }
            if (game.getGameSideB() != null) {
            	betView.setGameSideBId(game.getGameSideB().getId());
            }
            betViewssByGameId.put(game.getId(), betView);
            gamesByOrder.put(game.getOrder(), game);
        }
        
        try {
            
            fis = new FileInputStream(betsFile);
            book = new XSSFWorkbook(fis);
            final XSSFSheet sheet = book.getSheetAt(2);

            // Groups games
            int matchNumber = 1;
            for (int rowIndex=7; rowIndex < 55; rowIndex++) {
                final BetView betView = this.processGroupsGameBet(sheet, rowIndex, matchNumber, gamesByOrder, betViewssByGameId);
                // If betId is not null, update the current bet instance
                final Integer betId = betIdsByGameId.get(betView.getGameId());
                
				this.save(betId, leagueId, login, betView.getGameId(), betView.getGameSideAId(),
						betView.getGameSideBId(), betView.getScoreA(), betView.getScoreB());
                matchNumber++;
            }
            
            // Second round
            for (int rowIndex=10; rowIndex < 40; rowIndex+=4) {
				final BetView betView = this.processPlayOffGameBet(sheet, rowIndex, matchNumber, this.secondRoundColumnName,
						gamesByOrder, betViewssByGameId, gameSidesByName);
				// If betId is not null, update the current bet instance
                final Integer betId = betIdsByGameId.get(betView.getGameId());
            	this.save(betId, leagueId, login, betView.getGameId(), betView.getGameSideAId(),
						betView.getGameSideBId(), betView.getScoreA(), betView.getScoreB());
                matchNumber++;
            }
            
            // Quarter final round
            for (int rowIndex=12; rowIndex < 40; rowIndex+=8) {
            	final BetView betView = this.processPlayOffGameBet(sheet, rowIndex, matchNumber, this.quarterFinalsColumnName,
            			gamesByOrder, betViewssByGameId, gameSidesByName);
            	// If betId is not null, update the current bet instance
                final Integer betId = betIdsByGameId.get(betView.getGameId());
                this.save(betId, leagueId, login, betView.getGameId(), betView.getGameSideAId(),
						betView.getGameSideBId(), betView.getScoreA(), betView.getScoreB());
                matchNumber++;
            }
            // Semifinal round
            for (int rowIndex=16; rowIndex < 40; rowIndex+=16) {
            	final BetView betView = this.processPlayOffGameBet(sheet, rowIndex, matchNumber, this.semisColumnName,
            			gamesByOrder, betViewssByGameId, gameSidesByName);
            	// If betId is not null, update the current bet instance
                final Integer betId = betIdsByGameId.get(betView.getGameId());
                this.save(betId, leagueId, login, betView.getGameId(), betView.getGameSideAId(),
						betView.getGameSideBId(), betView.getScoreA(), betView.getScoreB());
                matchNumber++;
            }
            
            // Final
            final BetView betView = this.processPlayOffGameBet(sheet, 23, matchNumber, this.finalColumnName,
        			gamesByOrder, betViewssByGameId, gameSidesByName);
            // If betId is not null, update the current bet instance
            final Integer betId = betIdsByGameId.get(betView.getGameId());
            this.save(betId, leagueId, login, betView.getGameId(), betView.getGameSideAId(),
					betView.getGameSideBId(), betView.getScoreA(), betView.getScoreB());
            
            return;
        } finally {
            try {
                if (book != null) {book.close(); }
            } catch (final Exception e) { 
                // Nothing to do
            }
            try {
                if (fis != null) { fis.close(); }
            } catch (final Exception e) {
                // Nothing to do
            }
        }
    }
    
    private BetView processGroupsGameBet(final XSSFSheet sheet, final int rowIndex, final int matchNumber,
            final Map<Integer, Game> gamesByOrder,
            final Map<Integer, BetView> betsByGameId) {
        
        final CellReference cellReference = new CellReference(this.groupsFirstColumnName + rowIndex);
        final Row row = sheet.getRow(cellReference.getRow());
        final Cell homeResultCell = row.getCell(cellReference.getCol() + 1);
        final Cell awayResultCell = row.getCell(cellReference.getCol() + 2);
        
        final int homeResult = Double.valueOf(homeResultCell.getNumericCellValue()).intValue();
        final int awayResult = Double.valueOf(awayResultCell.getNumericCellValue()).intValue();
        
        final Game game = gamesByOrder.get(Integer.valueOf(matchNumber));
        final BetView betView = betsByGameId.get(game.getId());
        betView.setScoreA(Integer.valueOf(homeResult));
        betView.setScoreB(Integer.valueOf(awayResult));
        return betView;
    }
    
    private BetView processPlayOffGameBet(final XSSFSheet sheet, final int rowIndex, final int matchNumber,
            final String columnName, final Map<Integer, Game> gamesByOrder,
            final Map<Integer, BetView> betsByGameId,
            final Map<String, GameSide> gameSidesByName) {
    	
    	final CellReference homeCellReference = new CellReference(columnName + rowIndex);
    	final Row homeRow = sheet.getRow(homeCellReference.getRow());
    	final Cell homeTeamCell = homeRow.getCell(homeCellReference.getCol());
    	final Cell homeResultCell = homeRow.getCell(homeCellReference.getCol() + 1);
        final Cell extraTimeHomeResultCell = homeRow.getCell(homeCellReference.getCol() + 2);
        final Cell penaltiesHomeResultCell = homeRow.getCell(homeCellReference.getCol() + 3);
    	final String homeTeamName = homeTeamCell.getStringCellValue();
    	int homeResult = Double.valueOf(homeResultCell.getNumericCellValue()).intValue();
    	final int extraTimeHomeResult = Double.valueOf(extraTimeHomeResultCell.getNumericCellValue()).intValue();
        final int penaltiesHomeResult = Double.valueOf(penaltiesHomeResultCell.getNumericCellValue()).intValue();
    	
    	final CellReference awayCellReference = new CellReference(columnName + (rowIndex + 1));
    	final Row awayRow = sheet.getRow(awayCellReference.getRow());
    	final Cell awayTeamCell = awayRow.getCell(awayCellReference.getCol());
    	final Cell awayResultCell = awayRow.getCell(awayCellReference.getCol() + 1);
        final Cell extraTimeAwayResultCell = awayRow.getCell(homeCellReference.getCol() + 2);
        final Cell penaltiesAwayResultCell = awayRow.getCell(homeCellReference.getCol() + 3);
    	final String awayTeamName = awayTeamCell.getStringCellValue();
    	int awayResult = Double.valueOf(awayResultCell.getNumericCellValue()).intValue();
        final int extraTimeAwayResult = Double.valueOf(extraTimeAwayResultCell.getNumericCellValue()).intValue();
        final int penaltiesAwayResult = Double.valueOf(penaltiesAwayResultCell.getNumericCellValue()).intValue();
    	
        final Integer gameSideAId =
                gameSidesByName.get(homeTeamName) == null ? null : gameSidesByName.get(homeTeamName).getId();
        final Integer gameSideBId =
                gameSidesByName.get(awayTeamName) == null ? null : gameSidesByName.get(awayTeamName).getId();
    	
    	final Game game = gamesByOrder.get(Integer.valueOf(matchNumber));
    	final BetView betView = betsByGameId.get(game.getId());
    	betView.setGameSideAId(gameSideAId);
    	betView.setGameSideBId(gameSideBId);

    	if (homeResult == awayResult) {
    	    // We need to parse the extra time and maybe penalties results
            if (extraTimeHomeResult == extraTimeAwayResult) {
                // Penalties
                homeResult = penaltiesHomeResult > penaltiesAwayResult ? 1 : 0;
                awayResult = homeResult == 1 ? 0 : 1;
            }
        }

    	betView.setScoreA(Integer.valueOf(homeResult));
    	betView.setScoreB(Integer.valueOf(awayResult));
    	return betView;
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
