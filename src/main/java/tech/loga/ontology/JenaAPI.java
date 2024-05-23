package tech.loga.ontology;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.ontology.*;
import org.apache.jena.ontology.impl.OntologyImpl;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
public class JenaAPI {

    private static OntModel ONTOLOGY;

    @Autowired
    private JenaAPI(ResourceLoader resourceLoader){
        ONTOLOGY = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

        Resource resource = resourceLoader.getResource("classpath:/automaintongologie.owl");

        try {
            ONTOLOGY.read(resource.getInputStream(),null,null);
        } catch (IOException ex) {
            log.error("Ontology reading failed : \n{}",ex.getMessage());
        }
    }

    public static String getURI(){
        Iterator<Ontology> iter = ONTOLOGY.listOntologies();
        OntologyImpl onto = (OntologyImpl) iter.next();
        return (onto.getURI()+"#");
    }

    public String getString(ResultSet results){
        if (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            return soln.getLiteral("dysfonctionnement").getString();
        } else {
            return "No matching problem found in the ontology.";
        }
    }

    public List<Diagnosis> query(String text){
        String queryString = "PREFIX ontology: <"+getURI()+">\n" +
                "SELECT ?Titre ?Action \n" +
                "WHERE { \n" +
                " ?dysfonctionnement ontology:resoluPar ?maintenance .\n" +
                " ?dysfonctionnement ontology:Titre ?Titre .\n" +
                " ?maintenance ontology:Action ?Action .\n" +
                " FILTER regex( ?Titre , \"^" + text + "\") \n}";

        Query query = QueryFactory.create(queryString);

        QueryExecution qe = QueryExecutionFactory.create(query, ONTOLOGY);
        ResultSet results =  qe.execSelect();

        List<Diagnosis> result = new ArrayList<>();

        while (results.hasNext()){
            QuerySolution qs = results.next();
            Iterator<String> iter = qs.varNames();

            while(iter.hasNext()) {
                result.add(new Diagnosis(
                        String.valueOf(qs.get(iter.next())),
                        String.valueOf(qs.get(iter.next()))
                ));
            }
        }
        qe.close();
        return result;
    }

    public void updateOntology(List<Diagnosis> diagnoses ){
        ObjectProperty resoluPar = ONTOLOGY.getObjectProperty( getURI() + "resoluPar" );
        for (Diagnosis diagnosis:diagnoses) {
            Individual dysfun = addDysfunction(diagnosis.getDysfunction());
            Individual maint = addMaintenance(diagnosis.getMaintenance());
            dysfun.addProperty(resoluPar,maint);
        }
    }

    public Individual addDysfunction(String dysf){
        OntClass dysfunction = ONTOLOGY.getOntClass( getURI()+"Dysfonctionnement" );
        DatatypeProperty titre = ONTOLOGY.getDatatypeProperty(getURI()+"Titre");
        Individual individual = dysfunction.createIndividual(getURI()+dysf);
        individual.addProperty(titre,dysf);
        try {
            FileWriter out = new FileWriter("automaintongologie.owl");
            ONTOLOGY.write(out);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return individual;
    }

    public Individual addMaintenance(String maint){
        OntClass maintenance = ONTOLOGY.getOntClass( getURI()+"Maintenance");
        DatatypeProperty action = ONTOLOGY.getDatatypeProperty(getURI()+"Action");
        Individual individual = maintenance.createIndividual(getURI()+maint);
        individual.addProperty(action,maint);
        try {
            FileWriter out = new FileWriter("automaintongologie.owl");
            ONTOLOGY.write(out);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return individual;
    }
}

