package xsdgen.runtime;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Interface implémentée par toutes les classes Java générées.
 * Chaque classe générée sait construire son propre noeud DOM,
 * ce qui permet une sérialisation récursive simple.
 */
public interface XmlNode {

    /** Nom de la balise XML représentant cet objet (ex: "livre"). */
    String xmlTagName();

    /** Construit le noeud DOM (Element) correspondant à cet objet. */
    Element toXmlElement(Document doc);
}
