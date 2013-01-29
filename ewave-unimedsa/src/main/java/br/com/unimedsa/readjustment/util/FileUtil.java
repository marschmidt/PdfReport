package br.com.unimedsa.readjustment.util;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * <p>
 * This class helps verifying if a file or a directory exists, can be read and,
 * if specified, whether can be written.
 * </p>
 * 
 * @author Diogo Variani (diogo.variani@ewave.com.br)
 */
public class FileUtil {

    /**
     * The logger instance used to log the events occurred in this class.
     */
    private static Logger logger = Logger.getLogger(FileUtil.class);

    /**
     * Default value to check whether the file can be written. If the value is
     * <code>true</code> the class will check if the file is writable and
     * <code>false</code> is the opposite.
     */
    private static final Boolean CAN_WRITE = Boolean.FALSE;

    /**
     * @param filePath
     * @return
     */
    public static boolean checkFile(String filePath) {
	return checkFile(filePath, CAN_WRITE);
    }

    /**
     * @param filePath
     * @param canWrite
     * @return
     */
    public static boolean checkFile(String filePath, boolean canWrite) {
	// Checks if the filePath argument was specified
	if (filePath == null) {
	    throw new IllegalArgumentException("The 'filePath' must be specified");
	}

	File file = new File(filePath);
	return checkFile(file, canWrite);
    }

    /**
     * @param file
     * @return
     */
    public static boolean checkFile(File file) {
	return checkFile(file, CAN_WRITE);
    }

    /**
     * @param file
     * @param canWrite
     * @return
     */
    public static boolean checkFile(File file, boolean canWrite) {
	if (file == null) {
	    throw new IllegalArgumentException("The 'file' must be specified");
	}

	String filePath = file.getPath();

	logger.info(String.format("Trying to check the file %s", filePath));

	logger.debug(String.format("Checking if the file (%s) exists...", filePath));

	if (!file.exists()) {
	    logger.warn(String.format("The file path %s specified doesn't exist.", filePath));
	    return false;
	}

	logger.debug(String.format("Checking if the file (%s) is a directory...", filePath));
	
	if(file.isDirectory()){
		logger.warn(String.format("The file %s is a directory.", filePath));
	    return false;
	}
	
	logger.debug(String.format("Checking if the file (%s) can be read...", filePath));

	if (!file.canRead()) {
	    logger.warn(String.format("The file %s can not be read.", filePath));
	    return false;
	}

	if (canWrite) {
	    logger.debug(String.format("Checking if the file (%s) can be written...", filePath));

	    if (!file.canWrite()) {
		logger.warn(String.format("The file %s can not be written.", filePath));
		return false;
	    }
	}

	logger.info(String.format("The file %s is valid", filePath));

	return true;
    }

    /**
     * @param dirPath
     * @return
     */
    public static boolean checkDirectory(String dirPath) {
	return checkDirectory(dirPath, CAN_WRITE);
    }

    /**
     * @param dirPath
     * @param canWrite
     * @return
     */
    public static boolean checkDirectory(String dirPath, boolean canWrite) {
	if (dirPath == null) {
	    throw new IllegalArgumentException("The 'dirPath' must be specified");
	}

	File directory = new File(dirPath);
	return checkDirectory(directory, CAN_WRITE);
    }

    /**
     * @param directory
     * @return
     */
    public static boolean checkDirectory(File directory) {
	return checkDirectory(directory, CAN_WRITE);
    }

    /**
     * @param directory
     * @param canWrite
     * @return
     */
    public static boolean checkDirectory(File directory, boolean canWrite) {
	if (directory == null) {
	    throw new IllegalArgumentException("The 'directory' must be specified.");
	}

	String dirPath = directory.getPath();

	logger.info(String.format("Trying to check the directory %s", dirPath));

	logger.debug(String.format("Checking if the directory (%s) exists...",
		dirPath));

	if (!directory.exists()) {
	    logger.warn(String.format("The directory %s specified doesn't exist.", dirPath));
	    return false;
	}

	logger.debug(String.format(
		"Checking if the directory (%s) can be read...", dirPath));

	if (!directory.canRead()) {
	    logger.warn(String.format(
		    "The directory %s specified can not be read.", dirPath));
	    return false;
	}

	if (canWrite) {
	    logger.debug(String.format("Checking if the directory (%s) can be written...", dirPath));

	    if (!directory.canWrite()) {
		logger.warn(String.format("The directory %s can not be written.", dirPath));
		return false;
	    }
	}

	logger.info(String.format("The directory %s is valid", dirPath));
	return true;
    }
}
