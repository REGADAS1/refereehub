package com.regadas.refereehub.repository;

import com.regadas.refereehub.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}