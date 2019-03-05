package org.camunda.distributed.integration.client.commons;

import org.camunda.distributed.integration.commons.exceptions.GenericWfeIntegrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    public static final String FILE_SEPARATOR_SLASH = "/";
    public static final String FILE_SEPARATOR_BACKSLASH = "\\";
    public static final String TEMPORARY_DIRECTORY = "temp";

    public static final String OCTET_STREAM_MIME = "application/octet-stream";

    /**
     * Replaces file path separators with the ones used by current operating system
     *
     * @param filePath The String containing the filePath
     */
    public static String replaceSeparatorWithOSSeparator(String filePath) {
        if (File.separator.equals(FILE_SEPARATOR_SLASH)) {
            return filePath.replace(FILE_SEPARATOR_BACKSLASH, FILE_SEPARATOR_SLASH);
        }
        if (File.separator.equals(FILE_SEPARATOR_BACKSLASH)) {
            return filePath.replace(FILE_SEPARATOR_SLASH, FILE_SEPARATOR_BACKSLASH);
        }
        return filePath;
    }

    /**
     * Checks if a directory exists and if it doesn't it creates it.
     *
     * @param folder directory
     * @return true if directory exists or if it was successfully created
     */
    public static void createDirectoryIfNotExists(File folder) {
        if (!folder.exists()) {
            log.debug("Directories '" + folder.getAbsolutePath() + "' must be created.");
            Path directory;
            try {
                directory = Files.createDirectories(folder.toPath());
            } catch (SecurityException | IOException e) {
                throw new GenericWfeIntegrationException("Directories '" + folder.getAbsolutePath() + "' can not be created.", e);
            }

            if (!folder.exists()) {
                throw new GenericWfeIntegrationException("Directories '" + folder.getAbsolutePath() + "' not found after creation.");
            }

            if (directory == null) {
                log.debug("Directories '" + folder.getAbsolutePath() + "' could not be created. " + "Unknown exception.");
            } else {
                log.debug("Succeed to create directories '" + folder.getAbsolutePath() + "'.");
            }
        }
    }

    public static String uploadFileToLocation(InputStream inputStream, String destinationPath) {
        if (StringUtils.isEmpty(destinationPath)) {
            throw new GenericWfeIntegrationException("Can not create a file to a null or empty path.");
        }

        String checkedDestinationPath = replaceSeparatorWithOSSeparator(destinationPath);
        File file = new File(checkedDestinationPath);
        uploadFile(inputStream, file);
        return file.getAbsolutePath();
    }

    public static void uploadFile(InputStream inputStream, File destinationFile) {

        File destDir = destinationFile.getParentFile();
        createDirectoryIfNotExists(destDir);

        try {
            copyInputStream(inputStream, new FileOutputStream(destinationFile));
        } catch (FileNotFoundException e) {
            log.error("Can not open the output stream to copy the file.", e);
            throw new GenericWfeIntegrationException("Can not open the output stream to copy the file.", e);
        }
    }

    public static void copyInputStream(InputStream inputStream, OutputStream outputStream) {
        int bytesRead;
        byte[] buffer = new byte[1<<13];
        try {
            while ((bytesRead = inputStream.read(buffer, 0, 1<<13)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            throw new GenericWfeIntegrationException("Can not copy the file stream.", e);
        }
    }

    /**
     * Checks the path if the separators are available for the existing operating system
     *
     * @param filePath The String containing the filePath
     */
    public static String checkFilePath(String filePath) {
        if (File.separator.equals("/"))
            return filePath.replace('\\', '/');
        else if (File.separator.equals("\\"))
            return filePath.replace('/', '\\');
        else
            return filePath;
    }

    /**
     * Getting the path from a specific given path
     *
     * @param path The String containing the path
     */
    public static String getRealLocation(String path) {
        path = checkFilePath(path);

        path = path.trim();

        if (!path.endsWith(File.separator))
            path += File.separator;

        return path;
    }

    public static boolean fileExists(File file) {
        boolean flag;

        if (log.isDebugEnabled())
            log.debug("Searching for file '" + file.getAbsolutePath() + "'.");

        if (!file.canRead()) {
            if (log.isDebugEnabled())
                log.debug("Not rights to read file at '" + file.getAbsolutePath() + "'.");
            return false;
        }
        try {
            flag = file.exists();
        } catch (SecurityException e) {
            if (log.isDebugEnabled())
                log.debug("File '" + file.getAbsolutePath() + "' can not be read.", e);
            return false;
        }
        if (!flag) {
            if (log.isDebugEnabled())
                log.debug("File '" + file.getAbsolutePath() + "' was not found.");
        } else {
            if (log.isDebugEnabled())
                log.debug("File '" + file.getAbsolutePath() + "' was found.");
        }
        return flag;
    }

    public static void deleteFileFromLocation(String uploadedFilePath) {
        if (uploadedFilePath == null || uploadedFilePath.trim().length() == 0) {
            throw new GenericWfeIntegrationException("Can not delete file with null or empty name.");
        }
        deleteFile(Paths.get(uploadedFilePath));
    }

    /**
     * Deletes a file having a given path
     *
     * @param filePath the path o f the file
     */
    public static void deleteFile(Path filePath) {
        try {

            if (fileExists(filePath.toFile())) {
                File parentFile = filePath.toFile().getParentFile();
                Files.delete(filePath);
                // delete empty directory
                if (parentFile.isDirectory() && Objects.requireNonNull(parentFile.list()).length == 0) {
                    parentFile.delete();
                }
            }
        } catch (IOException e) {
            throw new GenericWfeIntegrationException("Cannot delete file: " + filePath.toString(), e);
        }
    }

    /**
     * Gets the temporary path from project properties file
     */
    public static String getTemporaryPath(String docRootPath) {
        return getRealLocation(docRootPath + File.separator + TEMPORARY_DIRECTORY + File.separator);
    }
}
