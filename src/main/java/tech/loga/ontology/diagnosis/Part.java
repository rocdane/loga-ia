package tech.loga.ontology.diagnosis;

import jade.content.Concept;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Part implements Concept {
    private String name, role;
}
