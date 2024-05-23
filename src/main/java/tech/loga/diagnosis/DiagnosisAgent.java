package tech.loga.diagnosis;

import jade.core.Agent;

import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DiagnosisAgent extends Agent {

    @Override
    protected void setup() {
        super.setup();

        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(getAID());

        ServiceDescription serviceDesc = new ServiceDescription();
        serviceDesc.setType("DIAGNOSIS");
        serviceDesc.setName("DIAGNOSIS_AGENT");

        agentDescription.addServices(serviceDesc);

        try {
            DFService.register(this, agentDescription);
        } catch (FIPAException e) {
            log.error("Register DiagnosisAgent failed : \n {}",e.getACLMessage());
        }

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();

        addBehaviour(parallelBehaviour);
    }
}
