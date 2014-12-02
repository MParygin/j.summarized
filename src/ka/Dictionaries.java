package ka;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Set of dictionary
 */
public class Dictionaries {
    
    public static final List<Dictionary> list;
    
    private static final DocumentBuilderFactory factoryDOM = DocumentBuilderFactory.newInstance();
    private static final DocumentBuilder builderNoNamespace;

    static {
        // DOM
        factoryDOM.setIgnoringComments(true);
        factoryDOM.setNamespaceAware(false);
        factoryDOM.setValidating(false);
        factoryDOM.setXIncludeAware(false);
        try {
            builderNoNamespace = factoryDOM.newDocumentBuilder();
        } catch (Exception ex) {
            throw new RuntimeException("Cannot create DOM factory");
        }
        
        System.out.println("Parse dictionaries...");
        list = new ArrayList<>();
        File dir = new File("./dic");
        if (!dir.exists()) {
            throw new RuntimeException("Directory 'dir' is not exists");
        }
        if (!dir.isDirectory()) {
            throw new RuntimeException("File 'dir' is not a directory");
        }
        // each file
        for (File file : dir.listFiles()) {
            // skip not xml files
            String name = file.getName();
            if (!name.toLowerCase().endsWith(".xml")) {
                continue;
            }
            // parse xml
            try {
                Document document = builderNoNamespace.parse(file);
                list.add(new Dictionary(document));
            } catch (SAXException | IOException e) {
                System.err.println("Unable parse " + name + " as xml file");
            }
        }
    }
    
    public static JComboBox<Dictionary> getCombo() {
        JComboBox<Dictionary> result = new JComboBox<>(list.toArray(new Dictionary[list.size()]));
        return result;
    }
}
