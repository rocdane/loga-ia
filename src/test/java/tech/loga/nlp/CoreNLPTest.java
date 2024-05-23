package tech.loga.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.util.CoreMap;
import org.apache.jena.base.Sys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class CoreNLPTest {

    @Autowired
    private CoreNLP coreNLP;

    @Test
    public void testProcess(){
        String text = "La voiture a des problèmes avec le moteur qui surchauffe";

        CoreDocument document = coreNLP.process(text);

        StringBuilder tokens = new StringBuilder();

        for (CoreEntityMention em: document.entityMentions()) {
            System.out.println("\tdetected entity: \t"+em.text()+"\t"+em.entityType());
        }

        String tokensAndNERTags =
                document
                        .tokens()
                        .stream()
                        .map(token -> "("+token.word()+","+token.ner()+")")
                        .collect(Collectors.joining(" "));
        System.out.println(tokensAndNERTags);

        for (CoreSentence coreSentence : document.sentences()) {
            for (CoreLabel token : coreSentence.tokens()) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                tokens.append(word).append(" ");
            }
        }
        Assertions.assertEquals(text,tokens.toString().trim());
    }
}