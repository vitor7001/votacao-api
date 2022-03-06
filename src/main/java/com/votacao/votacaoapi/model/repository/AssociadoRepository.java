package com.votacao.votacaoapi.model.repository;

import com.votacao.votacaoapi.model.entity.Associado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssociadoRepository extends JpaRepository<Associado, Long> {
    Associado findByCpf(String cpf);
}
