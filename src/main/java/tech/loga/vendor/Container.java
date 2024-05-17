package tech.loga.vendor;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Container {

    public static void main(String[] args) {
        Runtime runtime =  Runtime.instance();
        Properties properties = new ExtendedProperties();
        properties.setProperty(Profile.GUI, "false");
        properties.setProperty(Profile.LOCAL_PORT,"32256");
        properties.setProperty(Profile.MAIN_PORT,"32256");
        properties.setProperty(Profile.CONTAINER_NAME, "LOGA");
        ProfileImpl impl = new ProfileImpl(properties);
        AgentContainer container = runtime.createMainContainer(impl);
        try {
            container.start();
        } catch (ControllerException e) {
            log.error("JADE Main container failed to start : \n {}",e.getMessage());
        }
    }
}
