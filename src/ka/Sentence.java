package ka;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Sentence
 */
public class Sentence {
    
    private boolean selected;
    public double score;
    
    private List<String> words;

    public Sentence() {
        this.words = new ArrayList<>();
    }
    
    public void addWord(String str) {
        if (str == null || str.isEmpty()) {
            return;
        }
        this.words.add(str);
    }
    
    public int getWordCount() {
        return this.words.size();
    }

    public void build(StringBuilder tmp) {
        for (String word : this.words) {
            tmp.append(word);
        }
    }
    
    public boolean isParagraph() {
        if (this.words.size() <= 2) {
            return false;
        }
        return "\n".equals(this.words.get(0)) && "\n".equals(this.words.get(1));
    }
    
    public void calcTermCount(Stem rule, Map<String, Integer> map) {
        for (String word : this.words) {
            String strip = rule.strip(word);
            if (strip.equals(" ")) {
                continue;
            }
            if (strip.equals("\n")) {
                continue;
            }
            if (map.containsKey(strip)) {
                map.put(strip, map.get(strip) + 1);
            } else {
                map.put(strip, 1);
            }
        }
    }

    public void grade(List<String> importantWords, Map<String, Integer> occ, Stem rule) {
        int n = 0;
        for (String word : this.words) {
            String strip = rule.strip(word);
            if (importantWords.contains(strip)) {
                n++; // index of word
                this.score += occ.get(strip) * keyVal(n);
            }
        }
    }
    
    private static int keyVal(int n) {
        if (n == 1) {
            return 3;
        }
        if (n == 2) {
            return 2;
        }
        if (n == 3) {
            return 2;
        }
        if (n == 4) {
            return 2;
        }
        return 1;
    }
}
