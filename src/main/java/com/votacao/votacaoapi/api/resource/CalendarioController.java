package com.votacao.votacaoapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.votacao.votacaoapi.api.dto.StatusDTO;
import com.votacao.votacaoapi.model.entity.Pauta;
import com.votacao.votacaoapi.service.PautaService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarioController {

    private static final String CRON_EVERY_MINUTE = "0 0/1 * 1/1 * ?";

    private final PautaService pautaService;

    private final QueueSender queueSender;

    @Scheduled(cron = CRON_EVERY_MINUTE)
    public void finalizarPautas(){
try {
    List<Pauta> pautas = pautaService.findAll();

    pautas.forEach(pauta -> {
        System.out.println("DATA " + pauta.getDataFim());
        Boolean dataExpirada = this.verificarDataExpirada(pauta.getDataFim());

        if (dataExpirada) {
            queueSender.send("A Pauta " + pauta.getDescricao() + " foi finalizada");
        }

    });
}catch (Exception ex){
    System.out.println("Erro ao finalizar pautas " + ex.getMessage());
}
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
}
