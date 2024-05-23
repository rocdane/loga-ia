package tech.loga.nlp;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import tech.loga.ontology.diagnosis.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class NLPAgent extends Agent {

    @Autowired
    private CoreNLP coreNLP;

    @Override
    protected void setup() {
        super.setup();
        getContentManager().registerLanguage(new SLCodec());
        getContentManager().registerOntology(DiagnosisOntology.getInstance());

        ServiceDescription serviceDesc = new ServiceDescription();
        serviceDesc.setType("NLP");
        serviceDesc.setName("NLP_AGENT");

        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(getAID());
        agentDescription.addServices(serviceDesc);

        try {
            DFService.register(this, agentDescription);
        }catch (FIPAException e) {
            log.error("Register NLPAgent failed : \n {}",e.getACLMessage());
        }

        MessageTemplate template =
                MessageTemplate
                        .and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                MessageTemplate.MatchSender(getAID("UI_AGENT")));

        addBehaviour(new AchieveREResponder(this, template){
            @Override
            protected ACLMessage prepareResponse(ACLMessage request){

                ACLMessage subRequest = new ACLMessage(ACLMessage.REQUEST);
                subRequest.addReceiver(getAID("ONTOLOGY_AGENT"));

                try {
                    Fix fix = (Fix) getContentManager().extractContent(request);
                    Dysfunction dysfunction = fix.getDysfunction();
                    subRequest.setContent(process(dysfunction.getSymptom()));
                } catch (Codec.CodecException | OntologyException e) {
                    throw new RuntimeException(e);
                }

                addBehaviour(new AchieveREInitiator(myAgent, subRequest){
                    @Override
                    protected void handleInform(ACLMessage inform){
                        String data = inform.getContent();
                        // Process the data
                        String result = "Processed data: " + data;

                        // Send the response back to ClientAgent
                        ACLMessage response = request.createReply();
                        response.setPerformative(ACLMessage.INFORM);
                        response.setContent(result);
                        send(response);
                    }

                    @Override
                    protected void handleFailure(ACLMessage failure) {
                        ACLMessage response = request.createReply();
                        response.setPerformative(ACLMessage.FAILURE);
                        response.setContent("Failed to retrieve data");
                        send(response);
                    }
                });

                return null; // Defer the response
            }
        });
    }

    private String process(String dysfunction){
        CoreDocument document = coreNLP.process(dysfunction);

        for (CoreEntityMention em: document.entityMentions()) {
            System.out.println("\tdetected entity: \t"+em.text()+"\t"+em.entityType());
        }

        return document
                .tokens()
                .stream()
                .map(token -> "("+token.word()+","+token.ner()+")")
                .collect(Collectors.joining(" "));
    }

    private List<AID> getOntologyAgents() {
        List<AID> servicesAID = new ArrayList<>();
        DFAgentDescription agentDescription = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("ONTOLOGY");
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
