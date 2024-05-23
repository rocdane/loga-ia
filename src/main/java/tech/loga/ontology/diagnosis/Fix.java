package tech.loga.ontology.diagnosis;

import jade.content.AgentAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fix implements AgentAction {
    private Dysfunction dysfunction;
}
