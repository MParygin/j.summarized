package ka;

import java.util.ArrayList;
import java.util.List;

/**
 * The Stem Rule
 */
public class Stem {
    
    List<String> step1pre = new ArrayList<>();
    List<String> step1post = new ArrayList<>();
    List<String> manual = new ArrayList<>();
    List<String> pre = new ArrayList<>();
    List<String> post = new ArrayList<>();
    List<String> synonyms = new ArrayList<>();
    List<String> parserBreak = new ArrayList<>();
    List<String> parserDontBreak = new ArrayList<>();
    List<String> graderTc = new ArrayList<>();
    
    public boolean matchPost(String word, String post) {
        return word.toLowerCase().endsWith(post.toLowerCase());
    }
    
    public boolean parserShouldBreak(String word) {
        boolean result = false;
        for (String post : this.parserBreak) {
            if (matchPost(word, post)) {
                result = true;
                break;
            }
        }
        for (String post : this.parserDontBreak) {
            if (matchPost(word, post)) {
                result = false;
                break;
            }
        }
        return result;
    }
    
    public String strip(String word) {
        String norm = format(word);
        String original = norm;
        
        // manual
        for (String rule : this.manual) {
            String[] parts = split(rule);
            if (norm.equals(parts[0])) {
                norm = parts[1];
            }
        }
        // remove prefixes
        for (String rule : this.pre) {
            String[] parts = split(rule);
            if (norm.startsWith(parts[0])) {
                norm = parts[1].concat(norm.substring(parts[0].length()));
            }
        }
        // remove postfixes
        for (String rule : this.post) {
            String[] parts = split(rule);
            if (norm.endsWith(parts[0])) {
                norm = norm.substring(0, norm.length() - parts[0].length()).concat(parts[1]);
            }
        }
        // replace synonyms
        for (String rule : this.synonyms) {
            String[] parts = split(rule);
            if (norm.equals(parts[0])) {
                norm = parts[1];
            }
        }
        // if too short - return original
        if (norm.length() < 3) {
            return original;
        }
        return norm;
    }
    
    // remove prefixes & suffixes
    private String format(String word) {
        String norm = word.toLowerCase();
        for (String rule : this.step1pre) {
            String[] parts = split(rule);
            if (norm.startsWith(parts[0])) {
                norm = parts[1].concat(norm.substring(parts[0].length()));
            }
        }
        for (String rule : this.step1post) {
            String[] parts = split(rule);
            if (norm.endsWith(parts[0])) {
                norm = norm.substring(0, norm.length() - parts[0].length()).concat(parts[1]);
            }
        }
        return norm;
    }
    
    private static String[] split(String str) {
        String[] result = new String[2];
        int p = str.indexOf('|');
        result[0] = str.substring(0, p);
        result[1] = str.substring(p + 1);
        return result;
    }
    
    
}
