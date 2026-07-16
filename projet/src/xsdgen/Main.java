package xsdgen;

import xsdgen.model.XsdElement;
import java.io.File;

/**
 * Point d'entrée du générateur.
 * Usage : java xsdgen.Main <fichier.xsd> <elementRacine> <dossierSortie> <nomPackage>
 * Exemple : java xsdgen.Main xsd/bibliotheque.xsd bibliotheque src/generated generated
 */
public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("Usage: java xsdgen.Main <fichier.xsd> <elementRacine> <dossierSortie> <nomPackage>");
            return;
        }
        File xsdFile = new File(args[0]);
        String rootName = args[1];
        File outDir = new File(args[2]);
        String pkg = args[3];

        XsdParser parser = new XsdParser();
        XsdElement root = parser.parse(xsdFile, rootName);

        JavaCodeGenerator gen = new JavaCodeGenerator(pkg, outDir);
        gen.generate(root);

        System.out.println("Génération terminée avec succès.");
    }
}
