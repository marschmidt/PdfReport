package br.com.unimedsa.readjustment.report.export;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.query.XalanXPathQueryExecuterFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import br.com.unimedsa.readjustment.report.IReadjustmentReport;
import br.com.unimedsa.readjustment.report.ReportException;
import br.com.unimedsa.readjustment.util.FileUtil;

/**
 * <p>
 * This class is responsible for generating the PDF Readjustments Report for
 * Unimed SA. The class generates a report based on a source content (which contains a XML
 * source) and a path where the report will be created.
 * </p>
 * 
 * @author Diogo Variani (diogo.variani@ewave.com.br)
 */
public class PDFReadjustmentReport implements IReadjustmentReport {

    /**
     * The logger instance used to log the events occurred in this class.
     */
    private static Logger logger = Logger.getLogger(PDFReadjustmentReport.class);

    /**
     * Represents the namespace prefix specified in the schema of the source
     * content.
     */
    private static final String DEFAULT_NAMESPACE_PREFIX = "tns";

    /**
     * Represents the namespace specified in the schema of the source content.
     */
    private static final String DEFAULT_NAMESPACE = "http://www.unimedsa.com.br/customer/readjustment";

    /**
     * Represents the relative path to the schema of the source content.
     */
    private static final String SCHEMA_FILE = "/xsd/customer-readjustment.xsd";

    /**
     * Represents the relative path to the compiled report file.
     */
    private static final String COMPILED_REPORT_FILE = "/report-xml-datasource/template.jasper";

    /**
     * Represents the default report locale used in the report.
     */
    private static final Locale DEFAULT_LOCALE = new Locale("pt", "BR");
    
    /**
     * Represents the default date pattern used in the report.
     */
    private static final String DEFAULT_DATE_PATTERN = "dd/MM/yyyy";

    /**
     * Represents the default number pattern used in the report.
     */
    private static final String DEFAULT_NUMBER_PATTERN = "#,##0.##";

    /*
     * (non-Javadoc)
     * 
     * @see
     * br.com.unimedsa.readjustment.report.IReadjustmentReport#createReport(
     * java.lang.String, java.lang.String)
     */
    public void createReport(String source, String pathTarget) throws ReportException {
		// Checks if the source was specified
		if (source == null) {
		    throw new IllegalArgumentException("The source must be specified");
		}
	
		// Checks if the pathTarget was specified
		if (pathTarget == null) {
		    throw new IllegalArgumentException("The pathTarget must be specified");
		}
	
		// Checks the directory
		File file = new File(pathTarget);
		String directory = file.getParent();
		logger.debug(String.format("Checking if the directory %s is valid and if the application has permission to write...", directory));
		boolean checkDir = FileUtil.checkDirectory(directory, true);
	
		if (!checkDir) {
		    logger.error(String.format("The directory specified is not valid (%s), try to check the log for more details", directory));
		    throw new ReportException(String.format("The directory specified is not valid (%s), try to check the log for more details", directory));
		}
	
		// Checks the content
		logger.debug("Checking for a valid content...");
		boolean checkSource = checkSourceContent(source);
	
		if (!checkSource) {
		    logger.error("The sourceContent specified is not valid, try to validate the content against its schema.");
		    throw new ReportException("The sourceContent specified is not valid, try to validate the content against its schema.");
		}
	
		//Exporting the report
		exportReport(source, pathTarget);
    }

    /**
     * <p>It checks if the source is valid. To do this, the method valid the XML source spscieid by <code>source</code> against its own schema</p>. 
     * 
     * @param source the XML source that will be validate.
     * @return <code>true</code> if the content is valid and <code>false</code> otherwise.
     */
    private boolean checkSourceContent(String source) {
		assert source == null : "The source must be specified";
	
		StringReader inputStream = null;
		InputStream stream = null;
		//Validating the content
		try {
			logger.debug(String.format("Loading the schema file %s to use in validation...", SCHEMA_FILE));
			stream = this.getClass().getResourceAsStream(SCHEMA_FILE);
			
			inputStream = new StringReader(source);
			Source xmlFile = new StreamSource(inputStream);
			
			Source schemaFile = new StreamSource(stream);
			
		    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		    Schema schema = schemaFactory.newSchema(schemaFile);
		    Validator validator = schema.newValidator();
		    validator.validate(xmlFile);
		    logger.info("The content is valid");
		} catch (SAXException e) {
		    logger.warn("The content is NOT valid: " + e.getLocalizedMessage());
		    return false;
		} catch (IOException e) {
		    logger.error("The content is NOT valid: " + e.getLocalizedMessage());
		    return false;
		} finally{
			if(inputStream != null){
				inputStream.close();
			}
			
			if(stream != null){
				try {
					stream.close();
				} catch (IOException e) {
					logger.warn(String.format("Error closing the stream (%s)", SCHEMA_FILE), e);
				}
			}
		}
	
		return true;
    }

    /**
     * <p>This method is responsible for exporting the readjustments report. The report is created 
     * from a XML source specified by <code>source</code> and it is exported to the path specified 
     * by <code>targetPath</code>.</p>
     * 
     * @param source the XML source used to create the report.
     * @param targetPath where the report will be created.
     * @throws ReportException if there is any error when the report is generated.
     */
    private void exportReport(String source, String targetPath) throws ReportException {
    	
		assert source == null : "The source must be specified";
		assert targetPath == null : "The targetPath must be specified";
		
		FileOutputStream stream = null;
	
		try {
		    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		    dbf.setNamespaceAware(true);
		    DocumentBuilder db = dbf.newDocumentBuilder();
	
		    logger.debug("Converting the source to an input stream...");
		    ByteArrayInputStream inputStream = new ByteArrayInputStream(source.getBytes());
		    Document document = db.parse(inputStream);
	
		    // Getting the report parameters
		    Map<String, Object> parameters = getParameters();
		    parameters.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);
	
		    logger.debug("Filling the report with the parameters...");
		    
		    
			// Checks if the compiled report is in the classpath...
			InputStream compiledReportStream = getClass().getResourceAsStream(COMPILED_REPORT_FILE);
		
			if (compiledReportStream == null) {
			    String message = String.format("The compiled report file is not in classpath: %s", COMPILED_REPORT_FILE);
			    logger.error(message);
			    throw new ReportException(message);
			}
		    
		    JasperPrint jPrint = JasperFillManager.fillReport(compiledReportStream, parameters);
	
		    stream = new FileOutputStream(targetPath);
		    
		    logger.debug("Exporting the report...");
		    JRPdfExporter exporter = new JRPdfExporter();
		    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jPrint);
		    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, stream);
	
		    exporter.exportReport();
	
		    logger.info(String.format("The report was exported to %s", targetPath));
		} catch (ParserConfigurationException e) {
		    throw new ReportException("Error parsing the source content.", e);
		} catch (SAXException e) {
		    throw new ReportException("Error parsing the source content.", e);
		} catch (IOException e) {
		    throw new ReportException(String.format("The system can not write the content at the target path %s", targetPath), e);
		} catch (JRException e) {
		    throw new ReportException("Error creating the report", e);
		}finally{
			
			logger.debug("Closing the stream...");
			
			if(stream != null){
				try {
					stream.close();
				} catch (IOException e) {
					logger.warn(String.format("Error closing the stream (%s)", targetPath), e);
				}
			}
		}
    }

    /**
     * It creates the map of parameters to the report.
     * 
     * @return a map of parameters to the report.
     */
    private Map<String, Object> getParameters() {
	
		Map<String, String> namespaces = getNamespaceMap();
	
		logger.debug("Creating the map of parameters to export the report...");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(XalanXPathQueryExecuterFactory.PARAMETER_XML_NAMESPACE_MAP, namespaces);
		params.put(JRXPathQueryExecuterFactory.XML_DATE_PATTERN, DEFAULT_DATE_PATTERN);
		params.put(JRXPathQueryExecuterFactory.XML_NUMBER_PATTERN, DEFAULT_NUMBER_PATTERN);
		params.put(JRXPathQueryExecuterFactory.XML_LOCALE, DEFAULT_LOCALE);
		params.put(JRParameter.REPORT_LOCALE, DEFAULT_LOCALE);
	
		return params;
    }

    /**
     * It creates a map of namespaces which there will be in the XML source.
     * 
     * @return a map of namespeces.
     */
    private Map<String, String> getNamespaceMap() {
		logger.debug("Creating the namespace's map of the schema...");
	
		Map<String, String> namespaces = new HashMap<String, String>();
		namespaces.put(DEFAULT_NAMESPACE_PREFIX, DEFAULT_NAMESPACE);
	
		return namespaces;
    }
}
