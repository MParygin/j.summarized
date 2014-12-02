package ka;

import java.util.List;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The Dictionary
 */
public class Dictionary {

    String lang;
    Stem rule;
    
    Dictionary(Document document) {
        Element root = document.getDocumentElement();
        this.lang = root.getAttribute("lang");
        this.rule = new Stem();
        
        // stemmer
        Element eStemmer = getFirst(root, "stemmer");
        if (eStemmer != null) {
            node2list(eStemmer, "step1_pre", this.rule.step1pre, "rule");
            node2list(eStemmer, "step1_post", this.rule.step1post, "rule");
            node2list(eStemmer, "manual", this.rule.manual, "rule");
            node2list(eStemmer, "pre", this.rule.pre, "rule");
            node2list(eStemmer, "post", this.rule.post, "rule");
            node2list(eStemmer, "synonyms", this.rule.synonyms, "rule");
        }
        
        // parser
        Element eParser = getFirst(root, "parser");
        if (eParser != null) {
            node2list(eParser, "linebreak", this.rule.parserBreak, "rule");
            node2list(eParser, "linedontbreak", this.rule.parserDontBreak, "rule");
        }
        
        // grader-tc
        node2list(root, "grader-tc", this.rule.graderTc, "word");
    }
    
    private static  Element getFirst(Element element, String nodeName) {
        NodeList childs = element.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            Node node = childs.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (nodeName.equals(node.getNodeName())) {
                    return (Element) node;
                }
            }
        }
        return null;
    }

    private void node2list(Element root, String name, List<String> strings, String child) throws DOMException {
        Element eLinebreak = getFirst(root, name);
        if (eLinebreak != null) {
            NodeList list = eLinebreak.getElementsByTagName(child);
            for (int i = 0; i < list.getLength(); i++) {
                String s = list.item(i).getTextContent();
                strings.add(s);
            }
        }
    }
    
    @Override
    public String toString() {
        return this.lang;
    }
    
}
