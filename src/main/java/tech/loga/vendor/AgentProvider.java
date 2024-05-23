package tech.loga.vendor;

import lombok.Data;
import org.springframework.stereotype.Component;
import tech.loga.ui.UIAgent;

@Data
@Component
public class AgentProvider {
    private UIAgent uiAgent;
}
