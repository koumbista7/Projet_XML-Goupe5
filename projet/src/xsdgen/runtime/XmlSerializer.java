package xsdgen.runtime;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Petit moteur de sérialisation partagé par toutes les classes générées.
 * Construit un Document DOM à partir de l'objet racine puis l'écrit
 * sur disque avec une indentation lisible.
 */
public class XmlSerializer {

    public static void save(XmlNode root, String path) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = dbf.newDocumentBuilder().newDocument();
        Element rootEl = root.toXmlElement(doc);
        doc.appendChild(rootEl);

        File outFile = new File(path);
        if (outFile.getParentFile() != null) {
            outFile.getParentFile().mkdirs();
        }

        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        t.transform(new DOMSource(doc), new StreamResult(outFile));
    }
}
