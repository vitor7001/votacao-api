package com.votacao.votacaoapi.service;

import com.votacao.votacaoapi.model.entity.Pauta;
import com.votacao.votacaoapi.model.entity.Voto;

import java.util.List;
import java.util.Optional;

public interface VotoService {
    Voto save(Voto any);

    Optional<Voto> getById(Long id);

    List<Voto> votosDeUmaPauta(Long id);

    Voto findByPautaIdAndAssociadoId(Long id, Long id1);
}
