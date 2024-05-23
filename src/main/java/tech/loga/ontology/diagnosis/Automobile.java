package tech.loga.ontology.diagnosis;

import jade.content.Concept;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Automobile implements Concept {
    private String make, model;
}
