package tech.loga.vendor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.loga.ontology.JenaAPI;

@Slf4j
@Configuration
public class Config {

    @Bean
    CommandLineRunner init(){
        return  args -> log.info("Ontology : {}", JenaAPI.getURI());
    }
}
