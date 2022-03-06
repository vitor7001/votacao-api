package com.votacao.votacaoapi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@SpringBootApplication
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

	public static void main(String[] args) {
		SpringApplication.run(VotacaoApiApplication.class, args);
	}

}
