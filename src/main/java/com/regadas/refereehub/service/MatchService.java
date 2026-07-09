package com.regadas.refereehub.service;

import com.regadas.refereehub.domain.Match;
import com.regadas.refereehub.domain.MatchStatus;
import com.regadas.refereehub.dto.CreateMatchRequest;
import com.regadas.refereehub.dto.MatchResponse;
import com.regadas.refereehub.dto.UpdateMatchRequest;
import com.regadas.refereehub.exception.MatchNotFoundException;
import com.regadas.refereehub.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<MatchResponse> findAll(MatchStatus status, Integer year, Integer month) {
        List<Match> matches;

        boolean hasDateFilter = year != null && month != null;

        if (status == null && hasDateFilter) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            
            matches = matchRepository.findByStatusAndDateBetween(status, startDate, endDate);

        } else if (status != null) {
            matches = matchRepository.findByStatus(status);

        } else if (hasDateFilter) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            matches = matchRepository.findByDateBetween(startDate, endDate);

        } else {
            matches = matchRepository.findAll();
        }

        return matches.stream()
            .map(this::toResponse)
            .toList();
    }
    
    //PARA PROCURAR UM JOGO PELO ID
    public MatchResponse findById (Long id){
        Match match = matchRepository.findById(id)
            .orElseThrow(() ->  new MatchNotFoundException(id));

            return toResponse(match);
    }

    private MatchResponse toResponse(Match match) {
        return new MatchResponse(
                match.getId(),
                match.getDate(),
                match.getTime(),
                match.getRole(),
                match.getAgeGroup(),
                match.getDivision(),
                match.getHomeTeam(),
                match.getAwayTeam(),
                match.getVenue(),
                match.getStatus().name()
        );
    }

    public MatchResponse create(CreateMatchRequest request){
        Match match = new Match();

        match.setDate(request.date());
        match.setTime(request.time());
        match.setRole(request.role());
        match.setAgeGroup(request.ageGroup());
        match.setDivision(request.division());
        match.setHomeTeam(request.homeTeam());
        match.setAwayTeam(request.awayTeam());
        match.setVenue(request.venue());
        match.setStatus(request.status());

        Match savedMatch = matchRepository.save(match);

        return toResponse(savedMatch);
    }

    public MatchResponse update(Long id, UpdateMatchRequest request){
        Match match = matchRepository.findById(id)
            .orElseThrow(() -> new MatchNotFoundException(id));

        match.setDate(request.date());
        match.setTime(request.time());
        match.setRole(request.role());
        match.setAgeGroup(request.ageGroup());
        match.setDivision(request.division());
        match.setHomeTeam(request.homeTeam());
        match.setAwayTeam(request.awayTeam());
        match.setVenue(request.venue());
        match.setStatus(request.status());

        Match updatedMatch = matchRepository.save(match);

        return toResponse(updatedMatch);
    }

    public void delete(Long id){
        Match match = matchRepository.findById(id)
            .orElseThrow(() -> new MatchNotFoundException(id));

        matchRepository.delete(match);
    }

    


}