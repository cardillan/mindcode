@echo off

rem In order to prevent the important information from being obscured,
rem compile Mindcode, THEN give important information to get it working.
rem This way, if Mindcode can't start, the important information will be
rem available right above the failure.
mvnw.cmd install

echo "*****"
echo "*****"
echo "***** To run Mindcode locally, you need to have a PostgreSQL database accessible."
echo "***** You have a few choices:"
echo "*****"
echo "*****     - Install PostgreSQL for Windows: https://www.postgresql.org/download/windows/"
echo "*****"
echo "*****     - Use Docker to run PostgreSQL:"
echo "*****"
echo "*****           Download Docker from https://www.docker.com/get-started"
echo "*****           Once done, start a PostgreSQL instance:"
echo "*****               docker run -p 5432:5432 --name mindcode -v mindcode -e POSTGRES_HOST_AUTH_METHOD=trust postgres:latest"
echo "*****"
echo "***** If your PostgreSQL instance is not available at postgres://127.0.0.1:5432/mindcode_development, edit"
echo "***** both Spring configuration files and set the correct values:"
echo "*****    webapp\src\main\resources\application.properties"
echo "*****    webapp\src\test\resources\application.properties"
echo "*****"

java -classpath "$((Get-ChildItem .\webapp\ *.jar -Recurse | select -ExpandProperty FullName) -join ';')" info.teksol.mindcode.webapp.WebappApplication
