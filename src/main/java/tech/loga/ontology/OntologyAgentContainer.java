package tech.loga.ontology;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OntologyAgentContainer {

    private static AgentContainer ontologyAgentContainer;

    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();
        ProfileImpl impl = new ProfileImpl(false);
        impl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        impl.setParameter(ProfileImpl.CONTAINER_NAME, "ONTOLOGY_AGENT");
        ontologyAgentContainer = runtime.createAgentContainer(impl);
    }

    public OntologyAgentContainer(){
    }

    public AgentContainer getInstance(){
        return ontologyAgentContainer;
    }

    public static void createAgent(String username, boolean flag){
        try{
            AgentController agentController =
                    ontologyAgentContainer
                            .createNewAgent(username, OntologyAgent.class.getName(),new Object[]{flag});
            agentController.start();
        }catch(ControllerException e){
            log.error("Start Ontology Agent Container failed : \n{}",e.getMessage());
        }
    }
}
