# KeePass-AutoBackup
Automatic backup your KeePass file from your local storage to a network drive or any other directory every time you start KeePass. KeePass will load the last recent file from your backup location to ensure that changes from other devices are considered.

Main database gets copied to local and remote folder and KeePass will open the main database. If this database file can't be accessed KeePass will open the most recent local backup.

## Adjust Paths
You can either adjust the paths with command line arguments or through editing the `config.properties` file.

### Editing config with command line arguments
Format
```
PropertyName="Value"
```
#### Properties
|Property       |Description   |Example    |
|---------------|--------------|-----------|
|`PROGRAM`      |Location of KeePass.exe   |`"C:\Program Files (x86)\KeePass Password Safe 2\KeePass.exe"`
|`DATABASE`     |Location of the "main" database   |`"Z:\KeePass\KeePass.kdbx"`|
|`LOCAL_BACKUP` |Location of the local backup folder   |`"E:\Backups\KeePass"`|
|`REMOTE_BACKUP`|Location of the remote backup folder   |`"Z:\KeePass\Windows-Backup"`|

Note: A subdirectory with the current year will be created in both backup folders, resulting in e.g.: `E:\Backups\KeePass\2017\`

A full argument list could look like this:
```
PROGRAM="C:\Program Files (x86)\KeePass Password Safe 2\KeePass.exe"
DATABASE="Z:\KeePass\KeePass.kdbx"
LOCAL_BACKUP="E:\Backups\KeePass"
REMOTE_BACKUP="Z:\KeePass\Windows-Backup"
```

### Editing the config manually
The paths are stored in the file `config.properties` which is stored in the same directory as the program.
If you want to set the paths manually you have to escape backslashes and colons.

A full configuration could look like this:
```
LOCAL_BACKUP=E\:\\Backups\\KeePass
DATABASE=Z\:\\KeePass\\KeePass.kdbx
REMOTE_BACKUP=Z\:\\KeePass\\Windows-Backup
PROGRAM=D\:\\Program Files (x86)\\KeePass Password Safe 2\\KeePass.exe
```
## Project JavaDoc
See [Github Pages for KeePassAutoBackup](https://jonatanschneider.github.io/KeePassAutoBackup/main/java/Main.html)
