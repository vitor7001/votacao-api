package com.votacao.votacaoapi.model.repository;

import com.votacao.votacaoapi.model.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VotoRepository  extends JpaRepository<Voto, Long> {
    List<Voto> findByPauta_Id(long pautaId);

    Voto findByPauta_IdAndAssociado_Id(Long pautaId, Long associadoId);
}
