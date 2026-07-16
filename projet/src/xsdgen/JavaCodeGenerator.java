package xsdgen;

import xsdgen.model.XsdElement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Génère, pour chaque type complexe rencontré dans l'arbre XsdElement,
 * une classe Java autonome qui :
 *   - expose des attributs privés + getters/setters (encapsulation),
 *   - sait se sérialiser elle-même en noeud DOM (implements XmlNode),
 *   - si c'est la classe racine, expose en plus une méthode save(String).
 */
public class JavaCodeGenerator {

    private final String packageName;
    private final File outputDir;
    private final Set<String> generated = new HashSet<>();

    public JavaCodeGenerator(String packageName, File outputDir) {
        this.packageName = packageName;
        this.outputDir = outputDir;
    }

    public void generate(XsdElement root) throws IOException {
        outputDir.mkdirs();
        generateClass(root, true);
    }

    private void generateClass(XsdElement el, boolean isRoot) throws IOException {
        if (!el.isComplex()) return; // rien à générer pour une feuille simple
        if (generated.contains(el.getClassName())) return;
        generated.add(el.getClassName());

        // générer d'abord les classes enfants complexes (ordre de lecture plus naturel)
        for (XsdElement child : el.getChildren()) {
            if (child.isComplex()) generateClass(child, false);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("import xsdgen.runtime.XmlNode;\n");
        if (isRoot) sb.append("import xsdgen.runtime.XmlSerializer;\n");
        sb.append("import org.w3c.dom.Document;\n");
        sb.append("import org.w3c.dom.Element;\n");

        boolean needsList = el.getChildren().stream().anyMatch(XsdElement::isRepeatable);
        if (needsList) {
            sb.append("import java.util.ArrayList;\n");
            sb.append("import java.util.List;\n");
        }

        sb.append("\n/**\n * Classe générée automatiquement à partir de l'élément XSD \"")
          .append(el.getXmlName()).append("\".\n * Ne pas modifier à la main : régénérer via xsdgen.Main.\n */\n");
        sb.append("public class ").append(el.getClassName()).append(" implements XmlNode {\n\n");

        // ---- champs ----
        for (XsdElement child : el.getChildren()) {
            String fieldName = child.getFieldName();
            if (child.isComplex()) {
                if (child.isRepeatable()) {
                    String plural = fieldName + "s";
                    sb.append("    private List<").append(child.getClassName()).append("> ")
                      .append(plural).append(" = new ArrayList<>();\n");
                } else {
                    sb.append("    private ").append(child.getClassName()).append(" ")
                      .append(fieldName).append(";\n");
                }
            } else {
                String javaType = JavaTypeMapper.toJavaType(child.getSimpleType());
                sb.append("    private ").append(javaType).append(" ").append(fieldName).append(";\n");
            }
        }
        sb.append("\n");

        // ---- getters / setters ----
        for (XsdElement child : el.getChildren()) {
            String fieldName = child.getFieldName();
            String className = child.getClassName();
            if (child.isComplex()) {
                if (child.isRepeatable()) {
                    String plural = fieldName + "s";
                    String getterName = "get" + XsdElement.capitalize(plural);
                    sb.append("    public List<").append(className).append("> ").append(getterName)
                      .append("() { return ").append(plural).append("; }\n");
                } else {
                    String cap = XsdElement.capitalize(fieldName);
                    sb.append("    public ").append(className).append(" get").append(cap)
                      .append("() { return ").append(fieldName).append("; }\n");
                    sb.append("    public void set").append(cap).append("(").append(className)
                      .append(" ").append(fieldName).append(") { this.").append(fieldName)
                      .append(" = ").append(fieldName).append("; }\n");
                }
            } else {
                String javaType = JavaTypeMapper.toJavaType(child.getSimpleType());
                String cap = XsdElement.capitalize(fieldName);
                sb.append("    public ").append(javaType).append(" get").append(cap)
                  .append("() { return ").append(fieldName).append("; }\n");
                sb.append("    public void set").append(cap).append("(").append(javaType)
                  .append(" ").append(fieldName).append(") { this.").append(fieldName)
                  .append(" = ").append(fieldName).append("; }\n");
            }
        }
        sb.append("\n");

        // ---- xmlTagName ----
        sb.append("    @Override\n    public String xmlTagName() { return \"")
          .append(el.getXmlName()).append("\"; }\n\n");

        // ---- toXmlElement ----
        sb.append("    @Override\n    public Element toXmlElement(Document doc) {\n");
        sb.append("        Element el = doc.createElement(\"").append(el.getXmlName()).append("\");\n");
        for (XsdElement child : el.getChildren()) {
            String fieldName = child.getFieldName();
            if (child.isComplex()) {
                if (child.isRepeatable()) {
                    String plural = fieldName + "s";
                    sb.append("        for (").append(child.getClassName()).append(" item : ")
                      .append(plural).append(") {\n");
                    sb.append("            el.appendChild(item.toXmlElement(doc));\n");
                    sb.append("        }\n");
                } else {
                    sb.append("        if (").append(fieldName).append(" != null) {\n");
                    sb.append("            el.appendChild(").append(fieldName).append(".toXmlElement(doc));\n");
                    sb.append("        }\n");
                }
            } else {
                String varEl = fieldName + "El";
                sb.append("        Element ").append(varEl).append(" = doc.createElement(\"")
                  .append(child.getXmlName()).append("\");\n");
                sb.append("        ").append(varEl).append(".setTextContent(String.valueOf(")
                  .append(fieldName).append("));\n");
                sb.append("        el.appendChild(").append(varEl).append(");\n");
            }
        }
        sb.append("        return el;\n    }\n");

        if (isRoot) {
            sb.append("\n    /**\n     * Sérialise cet objet en fichier XML (méthode demandée par le sujet).\n     */\n");
            sb.append("    public void save(String path) throws Exception {\n");
            sb.append("        XmlSerializer.save(this, path);\n    }\n");
        }

        sb.append("}\n");

        File out = new File(outputDir, el.getClassName() + ".java");
        try (FileWriter fw = new FileWriter(out)) {
            fw.write(sb.toString());
        }
        System.out.println("Généré : " + out.getPath());
    }
}
