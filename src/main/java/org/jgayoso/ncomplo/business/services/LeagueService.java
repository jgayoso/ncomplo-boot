package org.jgayoso.ncomplo.business.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgayoso.ncomplo.business.entities.Bet;
import org.jgayoso.ncomplo.business.entities.BetType;
import org.jgayoso.ncomplo.business.entities.Competition;
import org.jgayoso.ncomplo.business.entities.Game;
import org.jgayoso.ncomplo.business.entities.GameSide;
import org.jgayoso.ncomplo.business.entities.League;
import org.jgayoso.ncomplo.business.entities.LeagueGame;
import org.jgayoso.ncomplo.business.entities.Round;
import org.jgayoso.ncomplo.business.entities.User;
import org.jgayoso.ncomplo.business.entities.repositories.BetRepository;
import org.jgayoso.ncomplo.business.entities.repositories.BetTypeRepository;
import org.jgayoso.ncomplo.business.entities.repositories.CompetitionRepository;
import org.jgayoso.ncomplo.business.entities.repositories.GameRepository;
import org.jgayoso.ncomplo.business.entities.repositories.LeagueRepository;
import org.jgayoso.ncomplo.business.util.I18nNamedEntityComparator;
import org.jgayoso.ncomplo.business.util.IterableUtils;
import org.jgayoso.ncomplo.business.views.ScoreMatterBetView;
import org.jgayoso.ncomplo.business.views.TodayEventsView;
import org.jgayoso.ncomplo.business.views.TodayRoundGamesAndBetsView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class LeagueService {

	@Autowired
	private LeagueRepository leagueRepository;

	@Autowired
	private CompetitionRepository competitionRepository;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private GameService gameService;
	
	@Autowired
	private BetTypeRepository betTypeRepository;

	@Autowired
	private BetRepository betRepository;
	
	@Autowired
	private EmailService emailService;

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
	public League save(final Integer id, final Integer competitionId, final String name,
			final Map<String, String> namesByLang, final String adminEmail, final boolean active,
			final Date betsDeadLine, final Map<Integer, Integer> betTypesByGame) {

		final Competition competition = this.competitionRepository.findOne(competitionId);

		final League league = (id == null ? new League() : this.leagueRepository.findOne(id));

		league.setCompetition(competition);
		league.setName(name);
		league.getNamesByLang().clear();
		league.getNamesByLang().putAll(namesByLang);
		league.setAdminEmail(adminEmail);
		league.setActive(active);
		league.getLeagueGames().clear();
		league.setBetsDeadLine(betsDeadLine);

		for (final Map.Entry<Integer, Integer> betTypesByGameEntry : betTypesByGame.entrySet()) {

			final Integer gameId = betTypesByGameEntry.getKey();
			final Integer betTypeId = betTypesByGameEntry.getValue();

			final Game game = this.gameRepository.findOne(gameId);
			final BetType betType = this.betTypeRepository.findOne(betTypeId);

			final LeagueGame leagueGame = new LeagueGame();
			leagueGame.setBetType(betType);
			leagueGame.setGame(game);
			leagueGame.setLeague(league);

			league.getLeagueGames().put(game, leagueGame);

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
			final List<Bet> bets = this.betRepository.findByLeagueIdAndUserLogin(league.getId(),
					participant.getLogin());
			for (final Bet bet : bets) {
				bet.evaluate();
			}
		}
	}
	
	public TodayEventsView getTodayInformation(final Integer leagueId) {
		
		final List<TodayRoundGamesAndBetsView> roundsInfo = new ArrayList<>();
		
		final List<Game> todayGames = this.gameService.findNextGames(leagueId);
		
		Map<Round, List<Game>> gamesByRound = new HashMap<Round, List<Game>>();
		for (Game game: todayGames){
			if (!gamesByRound.containsKey(game.getRound())) {
				gamesByRound.put(game.getRound(), new ArrayList<Game>());
			}
			gamesByRound.get(game.getRound()).add(game);
		}
		
		for (Entry<Round, List<Game>> roundGamesEntry: gamesByRound.entrySet()) {
			List<Game> games = roundGamesEntry.getValue();
			BetType betType = games.get(0).getDefaultBetType();
			TodayRoundGamesAndBetsView betView = null;
			if (betType.isScoreMatter()) {
				betView = this.processScoreMattersGames(leagueId, roundGamesEntry.getKey(), games);
			} else if (betType.isSidesMatter()) {
				betView = processSideMattersGames(leagueId, roundGamesEntry.getKey(), games);
			}
			if (betView != null) {
				roundsInfo.add(betView);
			}
		}
		
		if (!CollectionUtils.isEmpty(roundsInfo)) {
			Collections.sort(roundsInfo);
		}
		
		return new TodayEventsView(roundsInfo);
	}
	
	private TodayRoundGamesAndBetsView processScoreMattersGames(Integer leagueId, Round round, List<Game> games) {
		List<Bet> betsForGames = this.betRepository.findByScoreMatterTrueAndLeagueIdAndGameIn(leagueId, games);
		
		Map<String, Map<Integer, ScoreMatterBetView>> bets = new HashMap<>();
    	if (CollectionUtils.isEmpty(betsForGames)) {
    		return null;
    	}
		for (Bet bet: betsForGames) {
			String userLogin = bet.getUser().getLogin();
			if (!bets.containsKey(userLogin)) {
				bets.put(userLogin, new HashMap<Integer, ScoreMatterBetView>());
			}
			bets.get(userLogin).put(bet.getGame().getId(),
					new ScoreMatterBetView(userLogin, bet.getGame().getId(), bet.getScoreA(), bet.getScoreB()));
		}
    	return new TodayRoundGamesAndBetsView(round, games, bets, null, true);
	}
	
	private TodayRoundGamesAndBetsView processSideMattersGames(Integer leagueId, Round round, List<Game> games) {
		
		Collection<GameSide> gameSides = new HashSet<>();
    	for (Game game: games) {
    		gameSides.add(game.getGameSideA());
    		gameSides.add(game.getGameSideB());
    	}
		
		List<Bet> betsForRound = this.betRepository.findBySidesMatterTrueAndLeagueIdAndAndGameRound(leagueId, round);
		Map<String, List<GameSide>> sideMatterBets = new HashMap<>();
		int maxNumber = 0;
    	if (CollectionUtils.isEmpty(betsForRound)) {
    		return null;
    	}
		for (Bet bet: betsForRound) {
			String userLogin = bet.getUser().getLogin();
			if (!sideMatterBets.containsKey(userLogin)) {
				sideMatterBets.put(userLogin, new ArrayList<GameSide>());
			}
			if (bet.getScoreA().intValue() > bet.getScoreB().intValue() && gameSides.contains(bet.getGameSideA())) {
				sideMatterBets.get(userLogin).add(bet.getGameSideA());
			} else if (bet.getScoreA().intValue() < bet.getScoreB().intValue() && gameSides.contains(bet.getGameSideB())) {
				sideMatterBets.get(userLogin).add(bet.getGameSideB());
			}
		}
		for (Entry<String, List<GameSide>> userBets : sideMatterBets.entrySet()) {
			if (userBets.getValue().size() > maxNumber) {
				maxNumber = userBets.getValue().size();
			}
    	}
    	TodayRoundGamesAndBetsView betsView = new TodayRoundGamesAndBetsView(round, games, null, sideMatterBets, false);
    	betsView.setMaxSideMattersBets(maxNumber);
    	return betsView;
	}
	
	public void sendNotificationEmailToLeagueMembers(Integer leagueId, String subject, String text) {
		League league = this.leagueRepository.findOne(leagueId);
		
		Set<User> participants = league.getParticipants();
		if (CollectionUtils.isEmpty(participants)) {
			return;
		}
		
		String[] destinations = new String[participants.size()];
		int i = 0;
		for (User participant: participants) {
			destinations[i] = participant.getEmail();
			i++;
		}
		this.emailService.sendNotification(subject, destinations, text);
	}

}
