package br.com.unimedsa.readjustment.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.beanutils.locale.LocaleConvertUtils;
import org.apache.commons.beanutils.locale.LocaleConverter;
import org.apache.commons.beanutils.locale.converters.DecimalLocaleConverter;

import br.com.unimedsa.readjustment.util.FileUtil;


public class Main {

	private static final String SOURCE_PARAMETER = "-source";
	
	private static final String TARGET_PARAMETER = "-target";

	private String source;
	
	private String target;
	
	public void run(String[] args) {
		checkArguments(args);
		
		boolean checkFile = FileUtil.checkFile(source);
		if(!checkFile){
			throw new IllegalArgumentException(String.format("The source %s doesn't exist or the system doesn't have permission to read.", source));
		}
		
		String targetDir = new File(target).getParent();
		boolean checkDir = FileUtil.checkDirectory(targetDir, true);
		if(!checkDir){
			throw new IllegalArgumentException(String.format("The target directory %s doesn't exist or the system doesn't have permission to read.", targetDir));
		}
		
		IReadjustmentReportFactory factory = new ReadjustmentReportFactory();
		IReadjustmentReport report = factory.getReport(ExportType.PDF);
		
		File file = new File(source);

		StringBuffer content = new StringBuffer();
		byte[] buffer = new byte[1024];

		try {
			FileInputStream in = new FileInputStream(file);
			int size = -1;
			while ((size = in.read(buffer)) > 0) {
				content.append(new String(buffer, 0, size));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			report.createReport(content.toString(), target);
		} catch (ReportException e) {
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void checkArguments(String[] args) {
		
		boolean success = configureSource(args);
		
		configureTarget(args);
		
		if(!success){
			printHelp();
			System.exit(1);
		}
	}
	
	private boolean configureSource(String[] args){
		int index = 0;

		for (String arg : args) {
			if( arg.equals(SOURCE_PARAMETER)){
				
				if(args.length > (index + 1)) 
				{
					source = args[index + 1];
					return true;
				}else{
					return false;
				}
			}
			index++;
		}
		
		return false;
	}
	
	private void configureTarget(String[] args){
		int index = 0;

		for (String arg : args) {
			if( arg.equals(TARGET_PARAMETER)){
				
				if(args.length > (index + 1)) 
				{
					target = args[index + 1];
				}
				break;
			}
			index++;
		}
		
		if(target == null){
			try{
				target = File.createTempFile("readjustment-report", ".pdf", new File(".")).getAbsolutePath();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}

	private void printHelp() {
		StringBuilder builder = new StringBuilder();
		builder.append("-------------------------------------------------\n");
		builder.append("Unimed SA Readjusment Report Help.\n");
		builder.append("-------------------------------------------------\n\n");
		builder.append("java -cp [classpath] br.com.unimedsa.readjusment.report.Main [args]\n\n");
		builder.append("where arguments include:\n");
		builder.append("-source\tThe path for a file which contains the XML content to generate the report\n");
		builder.append("-target\tThe path where the report will be created.\n");
		
		System.out.println(builder.toString());
	}

	public static void main(String[] args) {
		new Main().run(args);
	}
}
