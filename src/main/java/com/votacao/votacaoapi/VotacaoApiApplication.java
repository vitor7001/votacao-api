package com.votacao.votacaoapi;

import com.votacao.votacaoapi.api.resource.QueueSender;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@SpringBootApplication
@EnableScheduling
@EnableRabbit
public class VotacaoApiApplication {

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

	@Bean
	public RestTemplate restTemplate(){
		return  new RestTemplate();
	}

	@Bean
	public Random random(){
		return  new Random();
	}

	@Bean
	QueueSender sender(){return new QueueSender();}

	public static void main(String[] args) {
		SpringApplication.run(VotacaoApiApplication.class, args);
	}

}
