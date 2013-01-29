package br.com.unimedsa.readjustment.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import org.apache.commons.beanutils.locale.LocaleConvertUtils;
import org.apache.log4j.Logger;

import br.com.unimedsa.readjustment.report.export.PDFReadjustmentReport;

/**
 * It represents a factory of readjustment report. It must create a readjustment
 * report from a export type.
 * 
 * @author Diogo Variani (diogo.variani@ewave.com.br)
 */
public class ReadjustmentReportFactory implements IReadjustmentReportFactory {

    /**
     * The logger instance used to log the events occurred in this class.
     */
    private static Logger logger = Logger.getLogger(ReadjustmentReportFactory.class);
	
	/* (non-Javadoc)
	 * @see br.com.unimedsa.readjustment.report.IReadjustmentReportFactory#getReport(br.com.unimedsa.readjustment.report.ExportType)
	 */
	public IReadjustmentReport getReport(ExportType type) {
		
		logger.debug(String.format("Checking the report for the type %s", type.name()));
		
		switch (type) {
			case PDF:
				return new PDFReadjustmentReport();
			default:
				String message = String.format("The type specified is not implemented yet: %s", type.name());
				throw new IllegalArgumentException(message);
		}
	}
}
