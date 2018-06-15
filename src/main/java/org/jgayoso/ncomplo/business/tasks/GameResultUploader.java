package org.jgayoso.ncomplo.business.tasks;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jgayoso.ncomplo.business.entities.Game;
import org.jgayoso.ncomplo.business.services.GameService;
import org.jgayoso.ncomplo.business.tasks.vo.MatchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

public class GameResultUploader {
	
	private static final Logger logger = Logger.getLogger(GameResultUploader.class);
	private static final RestTemplate restTemplate = new RestTemplate(); 
	
	@Autowired
	private GameService gameService;
	
	private final Integer competitionId;
	private final String endpoint;
	
	public GameResultUploader(final Integer competitionId, final String endpoint) {
		super();
		this.competitionId = competitionId;
		this.endpoint = endpoint;
	}
	
	@Scheduled(fixedRate=60000)
	public void updateCurrentGame(){
		if (this.competitionId == null || StringUtils.isBlank(this.endpoint)) {
			return;
		}
		
		final List<Game> todayGames = this.gameService.findAll(this.competitionId, Locale.ENGLISH);
		if (CollectionUtils.isEmpty(todayGames)) {
			return;
		}
		try {
			logger.debug("Invoking task");
			final ResponseEntity<MatchVO[]> responseEntity = restTemplate.getForEntity(this.endpoint, MatchVO[].class);
			
			if (responseEntity.hasBody()) {
				final MatchVO[] matches = responseEntity.getBody();
				logger.debug("Response: " + matches);
				for (final MatchVO match: matches) {
					for (final Game game: todayGames) {
						if (StringUtils.startsWithIgnoreCase(match.getHome_team().getCode(), game.getGameSideA().getCode())) {
							if (match.getHome_team().getGoals() != null && !match.getHome_team().getGoals().equals(game.getScoreA())) {
								game.setScoreA(match.getHome_team().getGoals());
								logger.debug("Game updated score A: " + game.getScoreA());
							}
							if (match.getAway_team().getGoals() != null && !match.getAway_team().getGoals().equals(game.getScoreB())) {
								game.setScoreB(match.getAway_team().getGoals());
								logger.debug("Game updated score B: " + game.getScoreB());
							}
						}
					}
				}
			} else {
				logger.debug("Empty response");
			}
		} catch (final Exception e) {
			logger.info("Error consuming service", e);
		}
		
	}

}
