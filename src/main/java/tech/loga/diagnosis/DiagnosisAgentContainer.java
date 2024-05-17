package tech.loga.diagnosis;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DiagnosisAgentContainer {

    private static AgentContainer diagnosisAgentContainer;

    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();
        ProfileImpl impl = new ProfileImpl(false);
        impl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        impl.setParameter(ProfileImpl.CONTAINER_NAME, "DIAGNOSIS_AGENT");
        diagnosisAgentContainer = runtime.createAgentContainer(impl);
    }

    public DiagnosisAgentContainer(){
    }

    public AgentContainer getInstance(){
        return diagnosisAgentContainer;
    }

    public static void createAgent(String username, boolean flag){
        try{
            AgentController agentController =
                    diagnosisAgentContainer
                            .createNewAgent(username, DiagnosisAgent.class.getName(),new Object[]{flag});
            agentController.start();
        }catch(ControllerException e){
            log.error("Start DiagnosisAgentContainer failed : \n{}",e.getMessage());
        }
    }
}
