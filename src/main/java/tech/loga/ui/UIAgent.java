package tech.loga.ui;

import jade.content.abs.AbsContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import tech.loga.ontology.diagnosis.DiagnosisOntology;
import tech.loga.ontology.diagnosis.Dysfunction;
import tech.loga.ontology.diagnosis.Fix;
import tech.loga.ontology.diagnosis.Maintenance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UIAgent extends Agent {

    @Override
    protected void setup() {
        getContentManager().registerLanguage(new SLCodec());
        getContentManager().registerOntology(DiagnosisOntology.getInstance());

        ServiceDescription serviceDesc = new ServiceDescription();
        serviceDesc.setType("UI");
        serviceDesc.setName("UI_AGENT");

        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(getAID());
        agentDescription.addServices(serviceDesc);

        try {
            DFService.register(this, agentDescription);
        } catch (FIPAException e) {
            log.error("Register UI Agent failed : \n {}",e.getACLMessage());
        }

        MessageTemplate messageTemplate =
                MessageTemplate
                        .and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                MessageTemplate.MatchSender(getAID("UI_AGENT")));

        addBehaviour(new AchieveREResponder(this, messageTemplate){
            @Override
            protected ACLMessage prepareResponse(ACLMessage request) {

                Fix fix = new Fix();
                fix.setDysfunction(new Dysfunction(request.getContent()));

                ACLMessage subRequest = new ACLMessage(ACLMessage.REQUEST);
                subRequest.addReceiver(getAID("NLP_AGENT"));
                for (AID agent:getNLPAgents()){
                    subRequest.addReceiver(agent);
                }
                subRequest.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
                subRequest.setOntology(DiagnosisOntology.ONTOLOGY_NAME);

                try {
                    getContentManager().fillContent(subRequest, new Action(getAID(), fix));
                } catch (Exception e) {
                    log.error("Take down UI Agent failed : \n {}", this.getClass().getName(),e);
                }

                addBehaviour(new AchieveREInitiator(myAgent, subRequest) {
                    @Override
                    protected void handleInform(ACLMessage inform) {
                        log.info("Received reply: {}", inform.getContent());
                        try {
                            ACLMessage response = request.createReply();
                            response.setPerformative(ACLMessage.INFORM);
                            Maintenance maintenance = (Maintenance) getContentManager().extractContent(inform);
                            getContentManager().fillContent(response, (AbsContentElement) maintenance);
                            send(response);
                        } catch (Codec.CodecException | OntologyException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    protected void handleFailure(ACLMessage failure) {
                        log.error("Processing failed : {}",failure.getContent());
                        ACLMessage response = request.createReply();
                        response.setPerformative(ACLMessage.FAILURE);
                        response.setContent("Failed to resolve dysfunction");
                        send(response);
                    }
                });

                return null;
            }
        });
    }

    public void sendMessage(String content, HttpServletResponse response) {
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(getAID("UI_AGENT"));
        request.setContent(content);

        new AchieveREInitiator(this, request){
            @Override
            protected void handleInform(ACLMessage inform) {
                try {
                    Maintenance maintenance = (Maintenance) getContentManager().extractContent(inform);
                    response.getOutputStream().print(maintenance.getAction());
                } catch (Codec.CodecException | OntologyException | IOException e) {
                    log.error("Diagnosis response failed", e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void handleFailure(ACLMessage failure) {
                try {
                    response.getOutputStream().print(failure.getContent());
                } catch (IOException e) {
                    log.error("Diagnosis failed : {}", failure.getContent());
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private List<AID> getNLPAgents() {
        List<AID> servicesAID = new ArrayList<>();
        DFAgentDescription agentDescription = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("NLP");
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
