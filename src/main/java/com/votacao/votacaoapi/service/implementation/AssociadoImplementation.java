package com.votacao.votacaoapi.service.implementation;

import com.votacao.votacaoapi.model.entity.Associado;
import com.votacao.votacaoapi.model.repository.AssociadoRepository;
import com.votacao.votacaoapi.service.AssociadoService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AssociadoImplementation implements AssociadoService {
    private AssociadoRepository repository;

    public AssociadoImplementation(AssociadoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Associado save(Associado any) {
        return repository.save(any);
    }

    @Override
    public Optional<Associado> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Associado getByCpf(String cpf) {
        return repository.findByCpf(cpf);
    }
}
