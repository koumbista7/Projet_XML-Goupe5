package generated;

import xsdgen.runtime.XmlNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Classe générée automatiquement à partir de l'élément XSD "livre".
 * Ne pas modifier à la main : régénérer via xsdgen.Main.
 */
public class Livre implements XmlNode {

    private String titre;
    private String auteur;
    private String editeur;

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }
    public String getEditeur() { return editeur; }
    public void setEditeur(String editeur) { this.editeur = editeur; }

    @Override
    public String xmlTagName() { return "livre"; }

    @Override
    public Element toXmlElement(Document doc) {
        Element el = doc.createElement("livre");
        Element titreEl = doc.createElement("titre");
        titreEl.setTextContent(String.valueOf(titre));
        el.appendChild(titreEl);
        Element auteurEl = doc.createElement("auteur");
        auteurEl.setTextContent(String.valueOf(auteur));
        el.appendChild(auteurEl);
        Element editeurEl = doc.createElement("editeur");
        editeurEl.setTextContent(String.valueOf(editeur));
        el.appendChild(editeurEl);
        return el;
    }
}
