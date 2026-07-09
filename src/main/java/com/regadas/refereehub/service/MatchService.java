package com.regadas.refereehub.service;

import com.regadas.refereehub.domain.Match;
import com.regadas.refereehub.dto.CreateMatchRequest;
import com.regadas.refereehub.dto.MatchResponse;
import com.regadas.refereehub.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<MatchResponse> findAll() {
        return matchRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
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

    //PARA PROCURAR UM JOGO PELO ID
    public MatchResponse findById (Long id){
        Match match = matchRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Match not found with id: " + id));

            return toResponse(match);
    }


}