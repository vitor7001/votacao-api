package com.votacao.votacaoapi.model.repository;

import com.votacao.votacaoapi.model.entity.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PautaRepository  extends JpaRepository<Pauta, Long> {
}
