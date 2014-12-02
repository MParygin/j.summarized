package ka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Article
 */
public class Article {

    private List<Sentence> sentencies;
    private Stem rule;
    
    
    public Article(Stem rule, String text) {
        this.sentencies = new ArrayList<>();
        this.rule = rule;
        
        // parse
        Sentence line = appendLine();
        
        StringBuilder buffer = new StringBuilder();
        for (char c : text.toCharArray()) {
            // skip \r
            if (c == '\r') continue;
            // space ?
            if (Character.isSpace(c)) {
                if (buffer.length() > 0) {
                    line.addWord(buffer.toString());
                    if (this.rule.parserShouldBreak(buffer.toString())) {
                        line = appendLine();
                    }
                    buffer.setLength(0);
                }
                if (c == '\n') {
                    line.addWord("\n");
                } else {
                    line.addWord(" ");
                }
                
            } else {
                buffer.append(c);
            }
        }
        // after loop
        if (buffer.length() > 0) {
            line.addWord(buffer.toString());
        }
    }
    
    public final Sentence appendLine() {
        Sentence result = new Sentence();
        this.sentencies.add(result);
        return result;
    }
    
    public int lineCount() {
        return this.sentencies.size();
    }
    
    public void calcTermCount(Map<String, Integer> map) {
        for (Sentence sentence : this.sentencies) {
            sentence.calcTermCount(this.rule, map);
        }
    }
    
    public double getMaxScore() {
        double max = 0;
        for (Sentence sentence : this.sentencies) {
            max = Math.max(max, sentence.score);
        }
        return max;
    }
    
    public String filter(double level) {
        StringBuilder tmp = new StringBuilder();
        for (Sentence sentence : this.sentencies) {
            if (sentence.score >= level) {
                sentence.build(tmp);
            }
        }
        return tmp.toString();
    }
    
    public void grade() {
        gradeTc();
        gradeStructure();
    }

    // Term Count
    private void gradeTc() {
        // 1) create word list
        Map<String, Integer> tc = new HashMap<>();
        calcTermCount(tc);
        
        // 0) remove ' ' & '\n'
        tc.remove(" ");
        tc.remove("\n");
        tc.remove("");
        
        // 2) remove all Dictionary words
        for (String word : this.rule.graderTc) {
            tc.remove(word);
        }
        
        // 3) get important words
        List<String> importantWords = new ArrayList<>(tc.keySet());
        
        // 4) grade
        for (Sentence sentence : this.sentencies) {
            sentence.grade(importantWords, tc, this.rule);
        }
    }

    private void gradeStructure() {
        // 1) increase *2 first line
        if (this.sentencies.size() > 0) {
            this.sentencies.get(0).score *= 2.0;
        }
        // 2) increase *1.6 each new paragraph
        for (Sentence sentence : this.sentencies) {
            if (sentence.isParagraph()) {
                sentence.score *= 1.6;
            }
        }
    }
    
}
