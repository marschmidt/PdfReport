package br.com.unimedsa.readjustment.report;

/**
 * It represents a factory of readjustment report. It must create a readjustment
 * report from a export type.
 * 
 * @author Diogo Variani (diogo.variani@ewave.com.br)
 */
public interface IReadjustmentReportFactory {

	/**
	 * It returns a readjusment report from a export type.
	 * 
	 * @param type the type of the export.
	 * @return the class responsible for export the readjusment report.
	 */
	public IReadjustmentReport getReport(ExportType type);
}
