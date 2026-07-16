package xsdgen;

import xsdgen.model.XsdElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lit un fichier XSD "simple" (éléments globaux + complexType/sequence)
 * et construit un arbre de XsdElement représentant la structure.
 *
 * Limites volontaires (le sujet demande de ne pas traiter les concepts
 * XSD trop avancés) :
 *  - un seul niveau de xs:schema, pas d'import/include
 *  - xs:complexType uniquement en tant qu'enfant direct d'un xs:element
 *    (pas de type nommé séparé réutilisé ailleurs)
 *  - xs:sequence uniquement (pas de xs:choice / xs:all)
 *  - xs:element ref="..." ou déclaration directe avec attribut type="xs:..."
 *  - pas de gestion des xs:attribute (uniquement des éléments enfants)
 */
public class XsdParser {

    private final Map<String, Element> topLevel = new LinkedHashMap<>();

    public XsdElement parse(File xsdFile, String rootName) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(xsdFile);
        doc.getDocumentElement().normalize();

        NodeList children = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && localName(n).equals("element")) {
                Element e = (Element) n;
                topLevel.put(e.getAttribute("name"), e);
            }
        }

        Element rootEl = topLevel.get(rootName);
        if (rootEl == null) {
            throw new IllegalArgumentException(
                "Élément racine '" + rootName + "' introuvable dans le fichier XSD.");
        }
        return build(rootEl, rootName);
    }

    private XsdElement build(Element el, String name) {
        XsdElement xe = new XsdElement(name);
        String typeAttr = el.getAttribute("type");
        Element complexType = child(el, "complexType");

        if (complexType != null) {
            xe.setComplex(true);
            Element sequence = child(complexType, "sequence");
            if (sequence != null) {
                NodeList seq = sequence.getChildNodes();
                for (int i = 0; i < seq.getLength(); i++) {
                    Node n = seq.item(i);
                    if (n.getNodeType() != Node.ELEMENT_NODE || !localName(n).equals("element")) continue;
                    Element childEl = (Element) n;
                    String ref = childEl.getAttribute("ref");
                    String maxOccurs = childEl.hasAttribute("maxOccurs") ? childEl.getAttribute("maxOccurs") : "1";
                    boolean repeatable = "unbounded".equals(maxOccurs) || parseIntSafe(maxOccurs) > 1;

                    Element target;
                    String childName;
                    if (ref != null && !ref.isEmpty()) {
                        target = topLevel.get(ref);
                        childName = ref;
                    } else {
                        target = childEl;
                        childName = childEl.getAttribute("name");
                    }
                    XsdElement childXe = build(target, childName);
                    childXe.setRepeatable(repeatable);
                    xe.getChildren().add(childXe);
                }
            }
        } else if (typeAttr != null && !typeAttr.isEmpty()) {
            xe.setSimpleType(typeAttr);
        }
        return xe;
    }

    private static Element child(Element parent, String tag) {
        NodeList nl = parent.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && localName(n).equals(tag)) {
                return (Element) n;
            }
        }
        return null;
    }

    private static String localName(Node n) {
        String ln = n.getLocalName();
        return ln != null ? ln : n.getNodeName().replaceFirst("^.*:", "");
    }

    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 1; }
    }
}
