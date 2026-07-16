package xsdgen;

/**
 * Table de correspondance entre les types simples XSD et les types Java.
 * Volontairement limitée aux types les plus courants (cf. consigne du sujet).
 */
public class JavaTypeMapper {

    public static String toJavaType(String xsdType) {
        if (xsdType == null) return "String";
        String t = xsdType.replaceFirst("^xs:", "");
        switch (t) {
            case "string":         return "String";
            case "int":
            case "integer":        return "int";
            case "boolean":        return "boolean";
            case "decimal":
            case "float":
            case "double":         return "double";
            case "date":
            case "dateTime":       return "String"; // simplifié volontairement
            default:               return "String";
        }
    }
}
