package org.jgayoso.ncomplo.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jgayoso.ncomplo.business.services.BetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UploadBetsController {
    
    @Autowired
    private BetService betService;
    
    public UploadBetsController() {
        super();
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/upload")
    public String uploadBets(@RequestParam("file") final MultipartFile file,
            @RequestParam("leagueId") final Integer leagueId,
            final RedirectAttributes redirectAttributes){
        final Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            /* The user is not logged in */
            redirectAttributes.addFlashAttribute("message", "Session expired");
            return "login";
        }
        
        final String login = auth.getName();
        
        try {
            final File betsFile = this.convert(file);
            this.betService.processBetsFile(betsFile, login, leagueId);
        } catch (final IOException e) {
            redirectAttributes.addFlashAttribute("message", "Error");
        }
        return "redirect:/scoreboard";
    }
    
    public File convert(final MultipartFile file) throws IOException {
        final File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        final FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

}
