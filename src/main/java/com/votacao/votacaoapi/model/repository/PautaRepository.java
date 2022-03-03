package com.votacao.votacaoapi.model.repository;

import com.votacao.votacaoapi.model.entity.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PautaRepository  extends JpaRepository<Pauta, Long> {
}
