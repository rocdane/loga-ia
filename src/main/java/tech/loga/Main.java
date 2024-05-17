package tech.loga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tech.loga.diagnosis.DiagnosisAgentContainer;
import tech.loga.nlp.NLPAgentContainer;
import tech.loga.ontology.OntologyAgentContainer;
import tech.loga.vendor.Container;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class,args);
        Container.main(null);
        DiagnosisAgentContainer.main(null);
        NLPAgentContainer.main(null);
        OntologyAgentContainer.main(null);
    }
}