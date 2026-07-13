package com.regadas.refereehub.repository;

import com.regadas.refereehub.domain.Match;
import com.regadas.refereehub.domain.MatchStatus;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByStatus(MatchStatus status);

    List<Match> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Match> findByStatusAndDateBetween(
            MatchStatus status,
            LocalDate startDate,
            LocalDate endDate
    );
}