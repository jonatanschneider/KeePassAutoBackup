package main.java;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author Jonatan Schneider
 * @version 1.0
 */
public class Main {
    /**
     * KeePass file extension
     **/
    private static final String EXTENSION = ".kdbx";
    /**
     * Name of the backup file
     **/
    private static final String FILE_NAME = File.separator + getCurrentDate() + EXTENSION;
    /**
     * KeePass.exe location
     **/
    private static String PROGRAM;
    /**
     * Database location
     **/
    private static Path DATABASE;
    /**
     * Folder of LOCAL backups
     **/
    private static Path LOCAL;
    /**
     * Folder of REMOTE backups
     **/
    private static Path REMOTE;

    public static void main(String[] args) {
        writeProperties(args);
        readProperties();
        try {
            createFolderIfNotExistent(LOCAL);
            createFolderIfNotExistent(REMOTE);
        } catch (IOException e) {
            System.out.println("Folders could not be found or created!");
            writeLog(e.getMessage());
        }
        try {
            createBackup(LOCAL);
            createBackup(REMOTE);
        } catch (IOException e) {
            System.out.println("Files could not be copied!");
            writeLog(e.getMessage());
        }
        try {
            start();
        } catch (IOException e) {
            System.out.println("KeePass could not be started!");
            writeLog(e.getMessage());
        }
    }

    /**
     * Start KeePass
     * If the REMOTE server is not reachable, the last LOCAL backup will be used
     *
     * @throws IOException When PROGRAM location can't be accessed
     */
    private static void start() throws IOException {
        //see https://stackoverflow.com/a/3774441/8040490
        String command = DATABASE.toString();
        if (Files.notExists(DATABASE)) {
            if (getLastModified(LOCAL) != null) {
                command = getLastModified(LOCAL).getPath();
            }
            command = ""; //no backup found for this year
        }
        Process keepass = Runtime.getRuntime().exec(PROGRAM + " " + command);
        keepass.getInputStream();
    }

    /**
     * Get current year through Year.now
     *
     * @return current year as int
     */
    private static int getCurrentYear() {

        return Year.now(ZoneId.systemDefault()).getValue();
    }

    /**
     * Get current date through LocalDate.now
     *
     * @return current date, formatted as YYYY-MM-DD
     */
    private static String getCurrentDate() {
        return LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Checks existence of folder and creates it if not existent
     *
     * @throws IOException if folder location can't be accessed
     */
    private static void createFolderIfNotExistent(Path path) throws IOException {
        if (Files.notExists(path)) Files.createDirectory(path);
    }

    /**
     * Get last modified file of a folder
     *
     * @param folder folder with backups
     * @return last modified file of 'folder'
     */
    private static File getLastModified(Path folder) {
        //https://stackoverflow.com/a/286001
        File f = new File(folder.toString());
        File[] files = f.listFiles(File::isFile);
        if (files == null) return null;

        long lastModified = Long.MIN_VALUE;
        File choice = null;
        for (File file : files) {
            if (file == null) continue;
            if (file.lastModified() > lastModified) {
                choice = file;
                lastModified = file.lastModified();
            }
        }
        return choice;
    }

    /**
     * Copy the DATABASE to 'folder' if it's not a duplicate
     *
     * @param folder backup folder
     * @return true if DATABASE got copied
     * @throws IOException if files can't be accessed
     */
    private static boolean createBackup(Path folder) throws IOException {
        if (Files.notExists(Paths.get(folder.toString() + FILE_NAME))) {
            //check whether DATABASE is newer or not
            long lastModified = (getLastModified(folder) != null ? getLastModified(folder).lastModified() : -1);
            if (lastModified < new File(DATABASE.toString()).lastModified()) {
                Files.copy(DATABASE, Paths.get(folder.toString() + FILE_NAME));
                return true;
            }
        }
        return false;
    }

    /**
     * Read paths from properties file and set variables
     */
    private static void readProperties() {
        File config = new File("config.properties");
        try (FileReader reader = new FileReader(config)) {
            Properties p = new Properties();
            p.load(reader);
            PROGRAM = p.getProperty("PROGRAM");
            DATABASE = Paths.get(p.getProperty("DATABASE"));
            LOCAL = Paths.get(p.getProperty("LOCAL_BACKUP") + getCurrentYear());
            REMOTE = Paths.get(p.getProperty("REMOTE_BACKUP") + getCurrentYear());
        } catch (java.io.FileNotFoundException e) {
            writeLog(e.getMessage());
        } catch (IOException e) {
            writeLog(e.getMessage());
        }
    }

    /**
     * Write paths to properties file
     * @param args command line arguments to be written to file
     */
    private static void writeProperties(String[] args) {
        Properties prop = new Properties();
        //write current properties
        if (Files.exists(Paths.get("config.properties"))) {
            try (FileReader fr = new FileReader("config.properties")) {
                prop.load(fr);
            } catch (IOException e) {
                writeLog(e.getMessage());
            }
        }
        //overwrite properties if arguments are passed
        String clArgs = parseArguments(args);
        if (!clArgs.isEmpty()) {
            try {
                prop.load(new StringReader(clArgs));
            } catch (IOException e) {
                writeLog(e.getMessage());
            }
        }
        //wrtie to properties file
        try (FileWriter fw = new FileWriter("config.properties")) {
            prop.store(fw, "see readme for information how to change paths");
        } catch (IOException e) {
            writeLog(e.getMessage());
        }
    }

    /**
     * Parse command line arguments to replace "\" with "\\" and add a line break after each argument
     * @param args command line arguments
     * @return parsed string
     */
    private static String parseArguments(String[] args) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(args)
                .map(arg -> arg.replace("\\", "\\\\"))
                .forEach(arg -> sb.append(arg).append("\n"));
        return sb.toString();
    }

    /**
     * Writes error messages to logfile
     * @param message error message
     */
    private static void writeLog(String message) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("error.log"))) {
            bw.write(message);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error in creating logfile");
            System.out.println(e.getMessage());
        }
    }
}