package com.regadas.refereehub.repository;

import com.regadas.refereehub.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByMatchId(Long matchId); //procurar pagamento por jogo

    boolean existsByMatchId(Long matchId); // verificar se um jogo já tem pagamento

    List<Payment> findByPaidFalse(); // procurar todos os pagamentos que não foram pagos

    List<Payment> findByMatchIdIn(List<Long> matchIds); // procurar todos os pagamentos de uma lista de jogos
}