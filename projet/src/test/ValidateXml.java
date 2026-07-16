import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

/**
 * Valide un fichier XML par rapport à un fichier XSD.
 * Usage: java ValidateXml <fichier.xsd> <fichier.xml>
 */
public class ValidateXml {
    public static void main(String[] args) throws Exception {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new File(args[0]));
        Validator validator = schema.newValidator();
        try {
            validator.validate(new StreamSource(new File(args[1])));
            System.out.println("VALIDE : " + args[1] + " respecte " + args[0]);
        } catch (Exception e) {
            System.out.println("INVALIDE : " + e.getMessage());
        }
    }
}
