package tech.loga.ontology.diagnosis;

import jade.content.Predicate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HasTrouble implements Predicate {
    private Part part;
    private Dysfunction dysfunction;
}
