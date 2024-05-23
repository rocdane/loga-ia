package tech.loga.vendor;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tech.loga.diagnosis.DiagnosisAgent;
import tech.loga.nlp.NLPAgent;
import tech.loga.ontology.OntologyAgent;
import tech.loga.ui.UIAgent;

@Slf4j
@Component
public class JadeConfig {

    private final AgentProvider agentProvider;

    @Autowired
    public JadeConfig(AgentProvider agentProvider) {
        this.agentProvider = agentProvider;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startJade(){
        Runtime runtime = Runtime.instance();
        Properties properties = new ExtendedProperties();
        properties.setProperty(Profile.GUI, "false");
        properties.setProperty(Profile.LOCAL_PORT,"32256");
        properties.setProperty(Profile.MAIN_PORT,"32256");
        properties.setProperty(Profile.CONTAINER_NAME, "LOGA");
        Profile profile = new ProfileImpl(properties);
        AgentContainer mainContainer = runtime.createMainContainer(profile);
        try {
            mainContainer.start();
            startUIAgent(mainContainer);
            startNLPAgent(mainContainer);
            startOntologyAgent(mainContainer);
            startDiagnosisAgent(mainContainer);
        } catch (ControllerException e) {
            log.error("Agent Main container failed to start : \n {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void startUIAgent(AgentContainer mainContainer){
        try {
            AgentController uiAgent = mainContainer.createNewAgent("GUI_AGENT", UIAgent.class.getName(), new Object[]{});
            uiAgent.start();
            agentProvider.setUiAgent(uiAgent.getO2AInterface(UIAgent.class));
        }catch (StaleProxyException e) {
            log.error("UI Agent container failed to start : \n {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void startNLPAgent(AgentContainer mainContainer){
        try {
            AgentController nlpAgent = mainContainer.createNewAgent("NLP_AGENT", NLPAgent.class.getName(), new Object[]{});
            nlpAgent.start();
        }catch (StaleProxyException e) {
            log.error("NLP Agent container failed to start : \n {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void startOntologyAgent(AgentContainer mainContainer){
        try {
            AgentController ontologyAgent = mainContainer.createNewAgent("ONTOLOGY_AGENT", OntologyAgent.class.getName(), new Object[]{});
            ontologyAgent.start();
        }catch (StaleProxyException e) {
            log.error("Ontology Agent container failed to start : \n {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void startDiagnosisAgent(AgentContainer mainContainer){
        try {
            AgentController diagnosisAgent = mainContainer.createNewAgent("DIAGNOSIS_AGENT", DiagnosisAgent.class.getName(), new Object[]{});
            diagnosisAgent.start();
        }catch (StaleProxyException e) {
            log.error("Diagnosis Agent container failed to start : \n {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
