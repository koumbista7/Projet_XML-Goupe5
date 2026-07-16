package xsdgen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un élément XSD (simple ou complexe) tel qu'extrait du schéma
 * par le XsdParser. C'est le modèle intermédiaire entre le XSD et le
 * générateur de code Java.
 */
public class XsdElement {

    private final String xmlName;     // nom tel qu'il apparaît dans le XML, ex: "livre"
    private final String className;   // nom de la classe Java générée, ex: "Livre"
    private String simpleType;        // type simple XSD (xs:string, xs:int, ...) si c'est une feuille
    private boolean complex;          // vrai si l'élément a un xs:complexType
    private boolean repeatable;       // vrai si maxOccurs > 1 ou "unbounded"
    private final List<XsdElement> children = new ArrayList<>();

    public XsdElement(String xmlName) {
        this.xmlName = xmlName;
        this.className = capitalize(xmlName);
    }

    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public String getXmlName() { return xmlName; }

    public String getClassName() { return className; }

    public String getFieldName() {
        return Character.toLowerCase(xmlName.charAt(0)) + xmlName.substring(1);
    }

    public String getSimpleType() { return simpleType; }
    public void setSimpleType(String simpleType) { this.simpleType = simpleType; }

    public boolean isComplex() { return complex; }
    public void setComplex(boolean complex) { this.complex = complex; }

    public boolean isRepeatable() { return repeatable; }
    public void setRepeatable(boolean repeatable) { this.repeatable = repeatable; }

    public List<XsdElement> getChildren() { return children; }
}
