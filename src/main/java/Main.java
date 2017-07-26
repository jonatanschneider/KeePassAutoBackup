import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Main {
    /**
     * KeePass database file extension
     **/
    private static final String extension = ".kdbx";
    /**
     * Name of the backup file
     **/
    private static final String fileName = File.separator + getCurrentDate() + extension;
    /**
     * KeePass.exe location
     **/
    private static String program = "D:\\Program Files (x86)\\KeePass Password Safe 2\\KeePass.exe";
    /**
     * Database location
     **/
    private static Path database = Paths.get("Z:\\KeePass\\KeePass.kdbx");
    /**
     * Folder of local backups
     **/
    private static Path local = Paths.get("E:\\Backups\\KeePass\\" + getCurrentYear());
    /**
     * Folder of remote backups
     **/
    private static Path remote = Paths.get("Z:\\KeePass\\Windows-Backup\\" + getCurrentYear());

    public static void main(String[] args) {
        try {
            createFolderIfNotExistent(local);
            createFolderIfNotExistent(remote);
        } catch (IOException e) {
            System.out.println("Folders could not be found or created!");
        }
        try {
            createBackup(local);
            createBackup(remote);
        } catch (IOException e) {
            System.out.println("Files could not be copied!");
        }

        try {
            start();
        } catch (IOException e) {
            System.out.println("KeePass could not be started!");
        }
    }

    /**
     * Start KeePass
     * If the remote server is not reachable, the last local backup will be used
     * @throws IOException When program location can't be accessed
     */
    private static void start() throws IOException {
        //see https://stackoverflow.com/a/3774441/8040490
        String command = database.toString();
        if(Files.notExists(database)){
            if(getLastModified(local) != null){
                command = getLastModified(local).getPath();
            }
            command = ""; //no backup found for this year
        }
        Process keepass = Runtime.getRuntime().exec(program + " " + command);
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
     * Checks existence of backup folders and creates them if not existent
     * @throws IOException if folder location can't be accessed
     */
    private static void createFolderIfNotExistent(Path path) throws IOException {
        if (Files.notExists(path)) Files.createDirectory(path);
    }

    /**
     * Get last modified file of a folder
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
     * Copy the database to 'folder' if it's not a duplicate
     * @param folder backup folder
     * @return true if database got copied
     * @throws IOException if files can't be accessed
     */
    private static boolean createBackup(Path folder) throws IOException {
        if (Files.notExists(Paths.get(folder.toString() + fileName))) {
            //check whether database is newer or not
            long lastModified = (getLastModified(folder) != null ? getLastModified(folder).lastModified() : -1);
            if (lastModified < new File(database.toString()).lastModified()) {
                Files.copy(database, Paths.get(folder.toString() + fileName));
                return true;
            }
        }
        return false;
    }
}