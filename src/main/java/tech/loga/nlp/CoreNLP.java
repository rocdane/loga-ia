package tech.loga.nlp;

import edu.stanford.nlp.pipeline.*;
import org.springframework.stereotype.Component;
import java.util.Properties;

@Component
public class CoreNLP {

    public CoreDocument process(String text){
        CoreDocument coreDocument = new CoreDocument(text);

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        pipeline.annotate(coreDocument);

        return coreDocument;
    }
}
