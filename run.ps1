<#!
Runs the JDBC Student Management System without requiring Maven.
The MySQL password is prompted for at runtime and is not saved to disk.
#>

$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$driverDirectory = Join-Path $projectRoot 'lib'
$driverFile = Join-Path $driverDirectory 'mysql-connector-j-8.4.0.jar'
$outputDirectory = Join-Path $projectRoot 'out'
$driverUrl = 'https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.4.0/mysql-connector-j-8.4.0.jar'

if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    throw 'Java JDK was not found. Install Java 17 or newer, then try again.'
}

if (-not (Test-Path $driverFile)) {
    New-Item -ItemType Directory -Force -Path $driverDirectory | Out-Null
    Write-Host 'Downloading MySQL Connector/J...'
    Invoke-WebRequest -Uri $driverUrl -OutFile $driverFile
}

$databaseUser = Read-Host 'MySQL username (press Enter for root)'
if ([string]::IsNullOrWhiteSpace($databaseUser)) {
    $databaseUser = 'root'
}

$securePassword = Read-Host 'MySQL password' -AsSecureString
$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)

try {
    $env:DB_USER = $databaseUser
    $env:DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)

    $sourceFiles = Get-ChildItem -Path (Join-Path $projectRoot 'src/main/java') -Recurse -Filter '*.java' |
        ForEach-Object FullName
    New-Item -ItemType Directory -Force -Path $outputDirectory | Out-Null
    & javac -Xlint:all -Werror -d $outputDirectory $sourceFiles
    if ($LASTEXITCODE -ne 0) {
        throw 'Compilation failed.'
    }

    & java -cp "$outputDirectory;$driverFile" com.prakash.jdbc.Application
} finally {
    if ($passwordPointer -ne [IntPtr]::Zero) {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
    }
    Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
}
