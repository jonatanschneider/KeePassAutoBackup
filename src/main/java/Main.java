import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        Path source = Paths.get("Z:\\KeePass\\KeePass.kdbx");
        String backupName = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE) + ".kdbx";
        Path localBackup = Paths.get("E:\\Backups\\KeePass\\" + Year.now(ZoneId.systemDefault()).getValue());
        Path remoteBackup = Paths.get("Z:\\KeePass\\Windows-Backup\\" + Year.now(ZoneId.systemDefault()).getValue());
        String fileName = "\\" + backupName;
        try {
            if(Files.notExists(localBackup)) Files.createDirectory(localBackup);
            if(Files.notExists(remoteBackup)) Files.createDirectory(remoteBackup);
            if(Files.notExists(Paths.get(localBackup + fileName))) Files.copy(source, Paths.get(localBackup + fileName));
            if(Files.notExists(Paths.get(remoteBackup + fileName))) Files.copy(source, Paths.get(remoteBackup + fileName));
        } catch (IOException e) {
            System.out.println("Files could not be copied!");
        }
        try {
            String command = "D:\\Program Files (x86)\\KeePass Password Safe 2\\KeePass.exe Z:\\KeePass\\KeePass.kdbx";
            Process keePass = Runtime.getRuntime().exec(command);
            keePass.getInputStream();
            //see https://stackoverflow.com/a/3774441/8040490
        } catch (IOException e) {
            System.out.println("KeePass could not be started!");
        }

    }
}
