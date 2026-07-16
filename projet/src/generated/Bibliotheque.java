package generated;

import xsdgen.runtime.XmlNode;
import xsdgen.runtime.XmlSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe générée automatiquement à partir de l'élément XSD "bibliotheque".
 * Ne pas modifier à la main : régénérer via xsdgen.Main.
 */
public class Bibliotheque implements XmlNode {

    private List<Livre> livres = new ArrayList<>();

    public List<Livre> getLivres() { return livres; }

    @Override
    public String xmlTagName() { return "bibliotheque"; }

    @Override
    public Element toXmlElement(Document doc) {
        Element el = doc.createElement("bibliotheque");
        for (Livre item : livres) {
            el.appendChild(item.toXmlElement(doc));
        }
        return el;
    }

    /**
     * Sérialise cet objet en fichier XML (méthode demandée par le sujet).
     */
    public void save(String path) throws Exception {
        XmlSerializer.save(this, path);
    }
}
