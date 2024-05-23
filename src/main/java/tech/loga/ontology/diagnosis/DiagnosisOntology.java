package tech.loga.ontology.diagnosis;

import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.PredicateSchema;

public class DiagnosisOntology extends BeanOntology {

    public static final String ONTOLOGY_NAME = "DIAGNOSIS-ONTOLOGY";

    private static Ontology instance;

    public static Ontology getInstance() {
        if(instance==null){
            instance = new DiagnosisOntology();
        }
        return instance;
    }

    private DiagnosisOntology() {
        super(ONTOLOGY_NAME);
        try {
            add(new ConceptSchema(Automobile.class.getSimpleName()), Automobile.class);
            add(new ConceptSchema(Part.class.getSimpleName()), Part.class);
            add(new ConceptSchema(Dysfunction.class.getSimpleName()), Dysfunction.class);
            add(new ConceptSchema(Maintenance.class.getSimpleName()), Maintenance.class);
            add(new PredicateSchema(HasPart.class.getSimpleName()), HasPart.class);
            add(new PredicateSchema(HasTrouble.class.getSimpleName()), HasTrouble.class);
            add(new PredicateSchema(HasFix.class.getSimpleName()), HasFix.class);
            add(new AgentActionSchema(Understand.class.getSimpleName()), Understand.class);
            add(new AgentActionSchema(Diagnose.class.getSimpleName()), Diagnose.class);
        } catch (OntologyException e) {
            throw new RuntimeException(e);
        }
    }
}
