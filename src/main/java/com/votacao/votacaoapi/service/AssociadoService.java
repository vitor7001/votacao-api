package com.votacao.votacaoapi.service;

import com.votacao.votacaoapi.model.entity.Associado;

import java.util.Optional;

public interface AssociadoService {
    Associado save(Associado any);

    Optional<Associado> getById(Long id);

    Associado getByCpf(String cpf);
}
