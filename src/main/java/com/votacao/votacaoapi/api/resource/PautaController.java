package com.votacao.votacaoapi.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.votacao.votacaoapi.api.dto.*;
import com.votacao.votacaoapi.model.entity.Associado;
import com.votacao.votacaoapi.model.entity.Pauta;
import com.votacao.votacaoapi.model.entity.Voto;
import com.votacao.votacaoapi.service.AssociadoService;
import com.votacao.votacaoapi.service.PautaService;
import com.votacao.votacaoapi.service.VotoService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@RequestMapping("/api/pautas")
@RestController
public class PautaController {

    private final PautaService service;
    private final VotoService serviceVoto;
    private final AssociadoService serviceAssociado;
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate;
    private final Random random;
    
    public PautaController(PautaService service, ModelMapper modelMapper,RestTemplate restTemplate,
                           Random random, VotoService serviceVoto, AssociadoService serviceAssociado) {
        this.service = service;
        this.modelMapper = modelMapper;
        this.restTemplate = restTemplate;
        this.random = random;
        this.serviceVoto = serviceVoto;
        this.serviceAssociado = serviceAssociado;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PautaDTO criar(@RequestBody @Valid PautaDTO dto){
        Pauta pautaASerSalva = modelMapper.map(dto, Pauta.class);

        pautaASerSalva = service.save(pautaASerSalva);

        return modelMapper.map(pautaASerSalva, PautaDTO.class);
    }

    @GetMapping
    public Page<PautaDTO> buscarPautas(PautaDTO dto, Pageable paginacao){
        Pauta filtro = modelMapper.map(dto, Pauta.class);

        Page<Pauta> pautas = service.find(filtro, paginacao);

        List<PautaDTO> listaDePautas = pautas.getContent().stream()
                .map(entidade -> modelMapper.map(entidade, PautaDTO.class))
                .collect(Collectors.toList());
        return new PageImpl<PautaDTO>(listaDePautas, paginacao, pautas.getTotalElements());
    }

    @GetMapping("{id}")
    public PautaComDadosDosVotosDTO buscar(@PathVariable Long id){

        PautaDTO pautaById = service.getById(id)
                .map(pauta -> modelMapper.map(pauta, PautaDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Voto> votosDaPauta = serviceVoto.votosDeUmaPauta(pautaById.getId());

        PautaComDadosDosVotosDTO pautaComDados = PautaComDadosDosVotosDTO.builder().build();

        pautaComDados.setVotosAFavor(this.contarVotos(votosDaPauta, true));
        pautaComDados.setVotosContra(this.contarVotos(votosDaPauta, false));

        return pautaComDados;
    }

    private int contarVotos(List<Voto> votosDaPauta, boolean votosAFavor) {

        int votos;
        if(votosAFavor){
            votos = (int) votosDaPauta.stream().filter(Voto::isVotou).count();
        }else{
            votos = (int) votosDaPauta.stream().filter(voto -> !voto.isVotou()).count();
        }
        return votos;
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id){
        Pauta pauta = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(pauta);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public PautaDTO atualizar(@PathVariable Long id,@RequestBody @Valid PautaDTO dto){
        Pauta pauta = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        pauta.setDescricao(dto.getDescricao());
        pauta = service.update(pauta);
        return modelMapper.map(pauta, PautaDTO.class);
    }

    @PutMapping(path="/iniciar/{id}",  produces= MediaType.APPLICATION_JSON_VALUE)
    public String iniciarPauta(@PathVariable Long id, @RequestBody PautaDataFimDTO dto) throws Exception {
        Pauta pauta = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if(pauta.getDataFim() != null && !pauta.getDataFim().isEmpty()){
            StatusDTO situacao = StatusDTO.builder().status("Está pauta já tem uma data de fim indicada.").build();
            return new ObjectMapper().writeValueAsString(situacao);
        }

        if(dto.getDataFim() != null){
            pauta.setDataFim(dto.getDataFim());
        }else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String dataAtualMaisUmMinuto = LocalDateTime.now().plusMinutes(1).format(formatter);
            pauta.setDataFim(dataAtualMaisUmMinuto.toString());
        }

        service.update(pauta);
        StatusDTO situacao = StatusDTO.builder().status("Pauta inicializada com sucesso.").build();
        return new ObjectMapper().writeValueAsString(situacao);
    }

    @PostMapping(path = "/votar/{id}/{cpf}",  produces= MediaType.APPLICATION_JSON_VALUE)
    public String votar(@PathVariable Long id, @PathVariable String cpf, @RequestBody VotoDTO votoDto) throws Exception {

            Pauta pauta = service.getById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            if(pauta.getDataFim() == null){
                StatusDTO situacao = StatusDTO.builder().status("Está pauta não foi inicializada para ser votada.").build();
                return new ObjectMapper().writeValueAsString(situacao);
            }

            Optional<Associado> associadoPorCpf = Optional.ofNullable(serviceAssociado.getByCpf(cpf));

            if(associadoPorCpf.isPresent()){
                Voto associadoJaVotou = serviceVoto.findByPautaIdAndAssociadoId(pauta.getId(), associadoPorCpf.get().getId());
                if(associadoJaVotou.getId() != 0 ){
                    StatusDTO situacao = StatusDTO.builder().status("Você já votou nesta pauta.").build();
                    return new ObjectMapper().writeValueAsString(situacao);
                }
            }

            Boolean dataExpirada = this.verificarDataExpirada(pauta.getDataFim());

            if(dataExpirada){
                StatusDTO situacao = StatusDTO.builder().status("A data de fim desta pauta já expirou.").build();
                return new ObjectMapper().writeValueAsString(situacao);
            }

            Boolean situacaoCpf = this.verificarCpf(cpf);

            if(!situacaoCpf){
                StatusDTO situacao = StatusDTO.builder().status("Este cpf não é permitido votar").build();
                return new ObjectMapper().writeValueAsString(situacao);
            }


            Associado associado = Associado.builder().cpf(cpf).build();
            associado = serviceAssociado.save(associado);

            Voto voto = modelMapper.map(votoDto, Voto.class);
            voto.setPauta(pauta);
            voto.setAssociado(associado);
            voto =serviceVoto.save(voto);

            StatusDTO situacao = StatusDTO.builder().status("Seu voto foi computado.").build();
            return new ObjectMapper().writeValueAsString(situacao);
    }

    private Boolean verificarDataExpirada(String dataFim) {

        LocalDateTime dataAtual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime dataPauta = LocalDateTime.parse(dataFim, formatter);

        if(!dataAtual.isBefore(dataPauta)){
            return true;
        }
        return false;
    }

    private Boolean verificarCpf(String cpf) {
        try {
            StatusDTO statusCpf = restTemplate.getForObject("https://user-info.herokuapp.com/users/".concat(cpf), StatusDTO.class);

            Boolean cpfValido = false;
            if (statusCpf.getStatus().equals("UNABLE_TO_VOTE")) {
                cpfValido = false;
            } else if (statusCpf.getStatus().equals("ABLE_TO_VOTE")) {
                cpfValido = true;
            }
            
            return cpfValido;
        }catch (Exception ex){
            // caso ocorra erro na requisição de verificação do cpf
            // esta parte simula se o cpf é válido ou inválido apenas para fins didáticos para não ter que inserir
            // uma função que faça essa verificação

            int numero = random.nextInt(20);
            
            if(numero % 2 == 0 ){
                return true;
            }
            return false;
        }
    }

    @GetMapping(path="/users/{cpf}", produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String usuarioPodeVotar(@PathVariable String cpf) throws Exception {

        Boolean situacaoCpf = this.verificarCpf(cpf);

        if(!situacaoCpf){
            StatusDTO situacao = StatusDTO.builder().status("UNABLE_TO_VOTE").build();
            return new ObjectMapper().writeValueAsString(situacao);
        }

        StatusDTO situacao = StatusDTO.builder().status("ABLE_TO_VOTE").build();
        return new ObjectMapper().writeValueAsString(situacao);
    }
}
