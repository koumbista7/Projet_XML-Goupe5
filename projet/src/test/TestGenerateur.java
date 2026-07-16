import generated.Bibliotheque;
import generated.Livre;

/**
 * Test de bout en bout : recrée le fichier XML exemple du sujet
 * (3 livres) en utilisant UNIQUEMENT les classes générées, exactement
 * comme dans le code Java fourni dans l'énoncé.
 */
public class TestGenerateur {
    public static void main(String[] args) throws Exception {

        // --- reproduction exacte de l'exemple du sujet (1 livre) ---
        Livre livre = new Livre();
        livre.setTitre("toto");
        livre.setAuteur("titi");
        livre.setEditeur("tutu");

        Bibliotheque biblio = new Bibliotheque();
        biblio.getLivres().add(livre);
        biblio.save("output/exemple_sujet.xml");
        System.out.println("Fichier généré : output/exemple_sujet.xml");

        // --- reproduction du fichier XML instance à 3 livres fourni dans le sujet ---
        Bibliotheque biblio3 = new Bibliotheque();
        for (int i = 1; i <= 3; i++) {
            Livre l = new Livre();
            l.setTitre("titre " + i);
            l.setAuteur("auteur " + i);
            l.setEditeur("editeur " + i);
            biblio3.getLivres().add(l);
        }
        biblio3.save("output/bibliotheque_generee.xml");
        System.out.println("Fichier généré : output/bibliotheque_generee.xml");
    }
}
