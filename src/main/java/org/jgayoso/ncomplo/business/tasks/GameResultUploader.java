package org.jgayoso.ncomplo.business.tasks;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jgayoso.ncomplo.business.entities.Competition;
import org.jgayoso.ncomplo.business.entities.Game;
import org.jgayoso.ncomplo.business.entities.League;
import org.jgayoso.ncomplo.business.entities.repositories.CompetitionRepository;
import org.jgayoso.ncomplo.business.entities.repositories.LeagueRepository;
import org.jgayoso.ncomplo.business.services.GameService;
import org.jgayoso.ncomplo.business.services.LeagueService;
import org.jgayoso.ncomplo.business.tasks.vo.MatchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Component
public class GameResultUploader {
	
	private static final Logger logger = Logger.getLogger(GameResultUploader.class);
	private static final RestTemplate restTemplate = new RestTemplate(); 
	
	@Autowired
	private GameService gameService;
	
	@Autowired
	private LeagueRepository leagueRepository;
	@Autowired
	private LeagueService leagueService;

	@Autowired
	private CompetitionRepository competitionRepository;
	
	public GameResultUploader() {
		super();
	}
	
	@Scheduled(fixedRate = 60000)
	@Transactional
	public void updateCurrentGame(){

		final List<Competition> competitions = this.competitionRepository.findByActiveIsTrueAndUpdaterUriIsNotNull();
		if (CollectionUtils.isEmpty(competitions)) {
			return;
		}
		for (final Competition competition : competitions) {
			final Integer competitionId = competition.getId();
			final String uri = competition.getUpdaterUri();
			if (StringUtils.isBlank(uri)) {
				continue;
			}
			
			final List<League> leagues = this.leagueRepository.findByCompetitionId(competitionId);
			if (CollectionUtils.isEmpty(leagues)) {
				continue;
			}

			final List<Game> todayGames = this.gameService.findAll(competitionId, Locale.ENGLISH);
			if (CollectionUtils.isEmpty(todayGames)) {
				return;
			}

			try {
				logger.info("Invoking task form competition " + competitionId);
				final ResponseEntity<MatchVO[]> responseEntity = restTemplate.getForEntity(uri,
						MatchVO[].class);

				if (responseEntity.hasBody()) {
					final MatchVO[] matches = responseEntity.getBody();
					logger.info("Response: " + matches);

					final Set<Game> updatedGames = new HashSet<Game>();
					for (final MatchVO match : matches) {
						for (final Game game : todayGames) {
							if (!game.isTeamsDefined()) {
								logger.info("Discarding game " + game);
								continue;
							}
							if (StringUtils.equalsIgnoreCase(match.getHome_team().getCode(),
									game.getGameSideA().getCode())
									&& StringUtils.equalsIgnoreCase(match.getAway_team().getCode(),
											game.getGameSideB().getCode())) {

								if (match.getHome_team().getGoals() != null
										&& !match.getHome_team().getGoals().equals(game.getScoreA())) {
									game.setScoreA(match.getHome_team().getGoals());
									updatedGames.add(game);
									logger.debug("Game updated score A: " + game.getScoreA());
								}
								if (match.getAway_team().getGoals() != null
										&& !match.getAway_team().getGoals().equals(game.getScoreB())) {
									game.setScoreB(match.getAway_team().getGoals());
									updatedGames.add(game);
									logger.debug("Game updated score B: " + game.getScoreB());
								}
							}
						}
					}
					if (CollectionUtils.isNotEmpty(updatedGames)) {
						for (final League league : leagues) {
							this.leagueService.recomputeScoresForGames(league.getId(), updatedGames);
						}
					}
				} else {
					logger.debug("Empty response");
				}
			} catch (final Exception e) {
				logger.info("Error consuming service " + e.getMessage() + " "
						+ (ArrayUtils.isEmpty(e.getStackTrace()) ? "" : e.getStackTrace()[0]));
			}
		}
		
	}

}
