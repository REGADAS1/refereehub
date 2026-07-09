package com.regadas.refereehub.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.regadas.refereehub.domain.MatchStatus;
import com.regadas.refereehub.dto.CreateMatchRequest;
import com.regadas.refereehub.dto.MatchResponse;
import com.regadas.refereehub.dto.UpdateMatchRequest;
import com.regadas.refereehub.service.MatchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/matches")
public class MatchController {
        private final MatchService matchService;

        public MatchController(MatchService matchService) {
                this.matchService = matchService;
        }

        @GetMapping
        public List<MatchResponse> getMatches(@RequestParam(required = false) MatchStatus status, @RequestParam(required = false) Integer year, @RequestParam(required = false)  Integer month) {
                return matchService.findAll(status, year, month);
        }

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public MatchResponse createMatch(@Valid @RequestBody CreateMatchRequest request) {
                return matchService.create(request);
        }

        @GetMapping("/{id}")
        public MatchResponse getMatchById(@PathVariable Long id){
                return matchService.findById(id);      
        }

        @PutMapping("/{id}")
        public MatchResponse updateMatch(@PathVariable Long id, @Valid @RequestBody UpdateMatchRequest request) {
                return matchService.update(id, request);
        }
         
        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void deleteMatch(@PathVariable Long id){
                matchService.delete(id);
        }
}       
