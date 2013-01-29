package br.com.unimedsa.readjustment.report;

/**
 * <p>
 * This interface is responsable for creating reports from a source. The result
 * is stored in the target path specified.
 * </p>
 * 
 * @author Diogo Variani (diogo.variani@ewave.com.br)
 */
public interface IReadjustmentReport {

    /**
     * <p>
     * It creates a report from a <code>source</code>. The result is stored in
     * the path specified by <code>targetPath</code>.
     * </p>
     * 
     * @param source
     *            contains the datasource used to generate the report.
     * @param targetPath
     *            is the path where will be stored the report.
     * @throws ReportException
     *             if there is any error when the report is generated.
     */
    public void createReport(String source, String targetPath) throws ReportException;
}
