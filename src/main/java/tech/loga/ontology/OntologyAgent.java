package tech.loga.ontology;

import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import tech.loga.ontology.diagnosis.Dysfunction;
import tech.loga.ontology.diagnosis.Fix;
import tech.loga.ontology.diagnosis.HasTrouble;
import tech.loga.ontology.diagnosis.Part;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OntologyAgent extends Agent {

    @Autowired
    private JenaAPI jenaAPI;

    @Override
    protected void setup() {
        super.setup();

        ServiceDescription serviceDesc = new ServiceDescription();
        serviceDesc.setType("ONTOLOGY");
        serviceDesc.setName("ONTOLOGY_AGENT");

        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(getAID());
        agentDescription.addServices(serviceDesc);

        try {
            DFService.register(this, agentDescription);
        } catch (FIPAException e) {
            log.error("Register Ontology Agent failed : \n {}", e.getACLMessage());
        }

        MessageTemplate template =
                MessageTemplate
                        .and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                MessageTemplate.MatchSender(getAID("NLP_AGENT")));

        addBehaviour(new AchieveREResponder(this, template) {
            @Override
            protected ACLMessage prepareResponse(ACLMessage request) {
                try {
                    Action action = (Action) getContentManager().extractContent(request);
                    if (action.getAction() instanceof Fix fix) {

                        Dysfunction dysfunction = fix.getDysfunction();

                        jenaAPI.addDysfunction(dysfunction.getSymptom());

                        Part part = new Part();

                        HasTrouble hasTrouble = new HasTrouble();
                        hasTrouble.setDysfunction(dysfunction);
                        hasTrouble.setPart(part);

                        ACLMessage reply = request.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        getContentManager().fillContent(reply, hasTrouble);
                        send(reply);
                    }
                } catch (Exception e) {
                    log.error("{}", this.getClass().getName(),e);
                }

                ACLMessage response = request.createReply();
                response.setPerformative(ACLMessage.INFORM);
                response.setContent("Sample data from database");
                return response;
            }
        });
    }

    private List<AID> getDiagnosisAgents() {
        List<AID> servicesAID = new ArrayList<>();
        DFAgentDescription agentDescription = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("DIAGNOSIS");
        agentDescription.addServices(serviceDescription);
        try {
            DFAgentDescription[] descriptions = DFService.search(this, agentDescription);
            for (DFAgentDescription dfad : descriptions) {
                servicesAID.add(dfad.getName());
            }
        } catch (FIPAException ex) {
            log.error("{} failed ", this.getClass().getName(), ex);
        }
        return servicesAID;
    }
}
