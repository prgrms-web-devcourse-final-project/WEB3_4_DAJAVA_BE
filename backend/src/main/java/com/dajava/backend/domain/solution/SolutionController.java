package com.dajava.backend.domain.solution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SolutionController {

	@Autowired
	private SolutionService logService;

	@GetMapping("/api/ux-solution")
	public String getUXSolution(@RequestBody String solutionDto) {
		return logService.getAISolution(solutionDto);
	}
}