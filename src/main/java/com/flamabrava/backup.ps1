# Parámetros de conexión
$serverName = "RIANSENPAI\MSSQLSERVERNUEVO"  
$databaseName = "FLAMABRAVA"                # Nombre de la base de datos
$backupDir = "C:\Users\HP\Desktop\Flamabrava\Backups"                   # Directorio donde se guardará el backup
$backupFile = "$backupDir\$databaseName" + "_$(Get-Date -Format 'yyyyMMdd_HHmmss').bak"  


if (-not (Test-Path $backupDir)) {
    New-Item -ItemType Directory -Path $backupDir
}


$backupQuery = "BACKUP DATABASE [$databaseName] TO DISK = N'$backupFile' WITH INIT"

$connectionString = "Server=$serverName;Integrated Security=True"
Invoke-Sqlcmd -Query $backupQuery -ConnectionString $connectionString

Write-Host "Backup completado: $backupFile"


# powershell -ExecutionPolicy Bypass -File C:\Users\HP\Desktop\Flamabrava\backend\src\main\java\com\flamabrava/backup.ps1
# Para poder realizar el backup