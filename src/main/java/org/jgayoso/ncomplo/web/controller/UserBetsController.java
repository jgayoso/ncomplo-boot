package org.jgayoso.ncomplo.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jgayoso.ncomplo.business.entities.Bet;
import org.jgayoso.ncomplo.business.entities.Competition;
import org.jgayoso.ncomplo.business.entities.Game;
import org.jgayoso.ncomplo.business.entities.Game.GameComparator;
import org.jgayoso.ncomplo.business.entities.GameSide;
import org.jgayoso.ncomplo.business.entities.League;
import org.jgayoso.ncomplo.business.entities.LeagueGame;
import org.jgayoso.ncomplo.business.entities.User;
import org.jgayoso.ncomplo.business.services.BetService;
import org.jgayoso.ncomplo.business.services.GameService;
import org.jgayoso.ncomplo.business.services.LeagueService;
import org.jgayoso.ncomplo.business.services.UserService;
import org.jgayoso.ncomplo.business.util.I18nNamedEntityComparator;
import org.jgayoso.ncomplo.web.admin.beans.BetBean;
import org.jgayoso.ncomplo.web.admin.beans.ParticipationBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class UserBetsController {
	
	private static final Logger logger = Logger.getLogger(UserBetsController.class);
	
    @Autowired
    private LeagueService leagueService;
    @Autowired
    private GameService gameService;
    @Autowired
    private UserService userService;

    @Autowired
    private BetService betService;
    
    public UserBetsController() {
    	super();
    }
    
    @RequestMapping("/bets/{leagueId}/")
    public String manage(
    		@PathVariable(value="leagueId")
            final Integer leagueId,
            final ModelMap model,
            final HttpServletRequest request) {

        final Locale locale = RequestContextUtils.getLocale(request);
        final Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
			/* The user is not logged in */
			return "login";
		}
		/* The user is logged in */
		final String login = auth.getName();
        
        final League league = this.leagueService.find(leagueId);
        if (league.getBetsDeadLine().before(new Date())) {
        	logger.info("User " + login + " trying to edit the bets in league " + league.getId() + " - " + league.getName());
            return "redirect:/scoreboard";
        }
        
        final Competition competition = league.getCompetition();
        final User participant = this.userService.find(login);
        
        final List<GameSide> competitionGameSides =
                new ArrayList<>(competition.getGameSides());
        Collections.sort(competitionGameSides, new I18nNamedEntityComparator(locale));
        
        final List<Bet> bets =
                this.betService.findByLeagueIdAndUserLogin(leagueId, login, locale);
        
        final ParticipationBean participationBean = new ParticipationBean();
        participationBean.setLeagueId(leagueId);
        participationBean.setLogin(login);

        final List<Game> allGames = new ArrayList<>();
        for (final LeagueGame leagueGame : league.getLeagueGames().values()) {
            
            final BetBean betBean = new BetBean();
            final Game game = leagueGame.getGame();
            betBean.setBetTypeId(leagueGame.getBetType().getId());
            betBean.setGameId(game.getId());
            if (game.getGameSideA() != null) {
                betBean.setGameSideAId(game.getGameSideA().getId());
            }
            if (game.getGameSideB() != null) {
                betBean.setGameSideBId(game.getGameSideB().getId());
            }
            
            participationBean.getBetsByGame().put(game.getId(), betBean);
            
            allGames.add(game);
            
        }
        
        Collections.sort(allGames, new GameComparator(locale));
        
        if (bets != null && bets.size() > 0) {

            for (final Bet bet : bets) {
                
                final Game game = bet.getGame();
                final BetBean betBean = participationBean.getBetsByGame().get(game.getId());
                
                betBean.setId(bet.getId());
                if (bet.getGameSideA() != null) {
                    betBean.setGameSideAId(bet.getGameSideA().getId());
                }
                if (bet.getGameSideB() != null) {
                    betBean.setGameSideBId(bet.getGameSideB().getId());
                }
                betBean.setScoreA(bet.getScoreA());
                betBean.setScoreB(bet.getScoreB());
                
            }
            
        }
        final User user = this.userService.find(login);
        model.addAttribute("user", user);
        model.addAttribute("participation", participationBean);
        model.addAttribute("league", league);
        model.addAttribute("competition", competition);
        model.addAttribute("participant", participant);
        model.addAttribute("allGames", allGames);
        model.addAttribute("allGameSides", competitionGameSides);
        model.addAttribute("allBets", bets);
        
        return "managebets";
        
    }
    
    @RequestMapping("/bets/{leagueId}/save")
    public String save(final ParticipationBean participationBean){
    	final Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
			/* The user is not logged in */
			return "login";
		}

		final String login = participationBean.getLogin();
        final Integer leagueId = participationBean.getLeagueId();
        final League league = this.leagueService.find(participationBean.getLeagueId());
        if (league.getBetsDeadLine().before(new Date())) {
            logger.info("User " + login + " trying to edit the bets in league " + league.getId() + " - " + league.getName());
            return "redirect:/scoreboard";
        }
        
    	for (final BetBean betBean : participationBean.getBetsByGame().values()) {
            final Game game = this.gameService.find(betBean.getGameId());
            this.betService.save(
                    betBean.getId(),
                    leagueId,
                    login,
                    betBean.getGameId(),
                    game.getGameSideA() != null ? game.getGameSideA().getId() : betBean.getGameSideAId(),
            		game.getGameSideB() != null ? game.getGameSideB().getId() : betBean.getGameSideBId(),
                    betBean.getScoreA(),
                    betBean.getScoreB());
            
        }
		return "redirect:/scoreboard";
	}
    
    @RequestMapping(method = RequestMethod.POST, value = "/upload")
    public String uploadBets(@RequestParam("file") final MultipartFile file,
    		@RequestParam("leagueId") final Integer leagueId,
    		final HttpServletRequest request,
            final RedirectAttributes redirectAttributes){
        final Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            /* The user is not logged in */
            redirectAttributes.addFlashAttribute("message", "Session expired");
            return "login";
        }
        
        final String login = auth.getName();
        final Locale locale = RequestContextUtils.getLocale(request);

        final League league = this.leagueService.find(leagueId);
        if (league.getBetsDeadLine().before(new Date())) {
            logger.info("User " + login + " trying to edit the bets in league " + league.getId() + " - " + league.getName());
            return "redirect:/scoreboard";
        }

        try {
            final File betsFile = this.convert(file, login);
            if (!betsFile.exists() || betsFile.length() == 0) {
                redirectAttributes.addFlashAttribute("error", "Empty file");
                return "redirect:/bets/"+leagueId+"/";
            }
            this.betService.processBetsFile(betsFile, login, leagueId, locale);
            betsFile.delete();
        } catch (final IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error processing bets file");
        } finally {
        	// delete file
        }
        redirectAttributes.addFlashAttribute("message", "Bets processed successfully");
        return "redirect:/bets/"+leagueId+"/";
    }
    
    public File convert(final MultipartFile file, final String login) throws IOException {
        final File convFile = new File(login);
        convFile.createNewFile();
        final FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

}
