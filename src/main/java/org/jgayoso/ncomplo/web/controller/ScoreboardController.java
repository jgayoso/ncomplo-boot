package org.jgayoso.ncomplo.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ScoreboardController {

	
	public ScoreboardController() {
		super();
    }
	
	@RequestMapping("/")
	public String sayHello() {
		return "Hello World";
	}
	
}
