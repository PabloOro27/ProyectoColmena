@echo off 
echo Iniciando Sistema Colmena... 
echo. 
java -jar Colmena.jar 
if %errorlevel% neq 0 ( 
    echo. 
    echo ❌ Error al ejecutar la aplicacion 
    echo Verifica que Java este instalado correctamente 
    echo. 
    pause 
) 
