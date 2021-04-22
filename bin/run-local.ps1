### Compiles and starts the Mindcode Web-Application

## Does application.properties references a jdbc-Connection to an existing PostgreSQL DB?

Write-Host 'Testing JDBC-Connection Configuration' -ForegroundColor Yellow
# Get the application.properties Files
$WebAppSrcDir = Join-Path ((Get-Location).Path) '.\webapp\src\'
$AppPropFiles = Get-ChildItem -LiteralPath $WebAppSrcDir -Recurse 'application.properties'

# The RegEx which recognizes the JDBC Connection, e.g.
# spring.datasource.url=jdbc:postgresql://localhost:5432/mindcode_development
$JdbcRegEx = '(?<param>spring.datasource.url)\s*=(?<val>(?<Protocol>[a-zA-Z-_]+:[a-zA-Z-_]+):\/\/(?<Host>[^:]+)(?<PortInfo>:(?<PortNo>\d+))\/(?<DBName>[a-zA-Z-_]+)).*'

# In each File, test each spring.datasource.url setting
$AppPropFiles | % {
	# Read the application.properties File
	# and test if we have a spring.datasource.url setting
	$RgxTest = [Regex]::Match((Get-Content $_.FullName), $JdbcRegEx)
	If ($RgxTest.Success) {
		Write-Host "Config-File: $($_.FullName)"
		Write-Host "Testing JDBC-Target: $($RgxTest[0].Groups['Host'].Value):$($RgxTest[0].Groups['PortNo'].Value) … " -NoNewline
		If ((Test-NetConnection -ComputerName $RgxTest[0].Groups['Host'].Value -Port $RgxTest[0].Groups['PortNo'].Value -InformationLevel Quiet -WarningAction:SilentlyContinue) -eq $false) {
			Write-Host 'failed!' -ForegroundColor Red
			
			Write-Host @'
*****
***** To run Mindcode locally, you need to have a PostgreSQL database accessible.
***** You have a few choices:
*****
*****     - Install PostgreSQL for Windows: https://www.postgresql.org/download/windows/
*****
*****     - Use Docker to run PostgreSQL:
*****
*****           Download Docker from https://www.docker.com/get-started
*****           Once done, start a PostgreSQL instance:
*****               docker run -p 5432:5432 --name mindcode -v mindcode -e POSTGRES_HOST_AUTH_METHOD=trust postgres:latest
*****
***** If your PostgreSQL instance is not available at postgres://127.0.0.1:5432/mindcode_development, edit
***** both Spring configuration files and set the correct values:
*****    webapp\src\main\resources\application.properties
*****    webapp\src\test\resources\application.properties
*****
'@ -ForegroundColor Magenta
			
			break script
		} Else {
			Write-Host 'OK' -ForegroundColor Green
		}
	}
}


# In order to prevent the important information from being obscured,
# compile Mindcode, THEN give important information to get it working.
# This way, if Mindcode can't start, the important information will be
# available right above the failure.
.\mvnw.cmd install

# Stop if 'mvnw.cmd install' failed with an error
If ($LastExitCode -eq 1) {
	Break Script
}

# Start the Web Application
java -classpath "$((Get-ChildItem .\webapp\ *.jar -Recurse | select -ExpandProperty FullName) -join ';')" info.teksol.mindcode.webapp.WebappApplication
