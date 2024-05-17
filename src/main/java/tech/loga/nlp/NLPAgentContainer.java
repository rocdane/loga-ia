package tech.loga.nlp;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NLPAgentContainer {

    private static AgentContainer NLPAgentContainer;

    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();
        ProfileImpl impl = new ProfileImpl(false);
        impl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        impl.setParameter(ProfileImpl.CONTAINER_NAME, "NLP_AGENT");
        NLPAgentContainer = runtime.createAgentContainer(impl);
    }

    public NLPAgentContainer(){
    }

    public AgentContainer getInstance(){
        return NLPAgentContainer;
    }

    public static void createAgent(String username, boolean flag){
        try{
            AgentController agentController =
                    NLPAgentContainer
                            .createNewAgent(username, NLPAgent.class.getName(),new Object[]{flag});
            agentController.start();
        }catch(ControllerException e){
            log.error("Start NLPAgentContainer failed : \n{}",e.getMessage());
        }
    }
}
