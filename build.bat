@echo off
echo ==========================================
echo    COMPILANDO PROYECTO COLMENA COMPLETO
echo ==========================================

REM Crear directorio bin si no existe
if not exist "bin" mkdir bin

REM Limpiar compilaciones anteriores
if exist "bin\*" del /s /q bin\*

echo Compilando todos los archivos Java...

REM Compilar TODOS los archivos encontrados
javac -cp "lib\*;src" -d bin ^
    src\com\colmena\app\ColmenaApp.java ^
    src\com\colmena\controller\ClienteController.java ^
    src\com\colmena\controller\PedidoController.java ^
    src\com\colmena\controller\PrincipalController.java ^
    src\com\colmena\controller\ProductoController.java ^
    src\com\colmena\controller\UsuarioController.java ^
    src\com\colmena\data\ConexionBDD.java ^
    src\com\colmena\model\Categoria.java ^
    src\com\colmena\model\Cliente.java ^
    src\com\colmena\model\DetallePedido.java ^
    src\com\colmena\model\HistorialPago.java ^
    src\com\colmena\model\Pedido.java ^
    src\com\colmena\model\Producto.java ^
    src\com\colmena\model\ProductoHistorial.java ^
    src\com\colmena\model\Rol.java ^
    src\com\colmena\model\Usuario.java ^
    src\com\colmena\util\SecurityUtil.java ^
    src\com\colmena\view\Clientes\AgregarClienteDialog.java ^
    src\com\colmena\view\Clientes\ClientesPanel.java ^
    src\com\colmena\view\Clientes\EditarClienteDialog.java ^
    src\com\colmena\view\Pedido\NuevoPedidoDialog.java ^
    src\com\colmena\view\Pedido\PedidosClientePanel.java ^
    src\com\colmena\view\Principal\PantallaPrincipal.java ^
    src\com\colmena\view\Productos\AgregarProductoDialog.java ^
    src\com\colmena\view\Productos\EditarProductoDialog.java ^
    src\com\colmena\view\Productos\ProductosPanel.java ^
    src\com\colmena\view\Usuario\AgregarUsuarioDialog.java ^
    src\com\colmena\view\Usuario\EditarUsuarioDialog.java ^
    src\com\colmena\view\Usuario\LoginForm.java ^
    src\com\colmena\view\Usuario\UsuariosPanel.java

if %errorlevel% neq 0 (
    echo.
    echo ❌ ERROR EN LA COMPILACION!
    echo Revisa los errores mostrados arriba
    pause
    exit /b 1
)

echo ✅ Compilacion exitosa!
echo.

REM Crear el MANIFEST.MF
echo Creando archivo MANIFEST.MF...
echo Manifest-Version: 1.0 > MANIFEST.MF
echo Main-Class: com.colmena.app.ColmenaApp >> MANIFEST.MF
echo Class-Path: lib/mssql-jdbc-12.4.2.jre11.jar lib/jbcrypt-0.4.jar >> MANIFEST.MF
echo. >> MANIFEST.MF

REM Crear el JAR
echo Creando archivo JAR ejecutable...
jar cfm Colmena.jar MANIFEST.MF -C bin .

if %errorlevel% neq 0 (
    echo ❌ Error al crear el JAR!
    pause
    exit /b 1
)

echo.
echo ==========================================
echo    ✅ COLMENA.JAR CREADO EXITOSAMENTE!
echo ==========================================
echo.

REM Crear script de ejecución
echo Creando script de ejecucion...
echo @echo off > run_colmena.bat
echo echo Iniciando Sistema Colmena... >> run_colmena.bat
echo echo. >> run_colmena.bat
echo java -jar Colmena.jar >> run_colmena.bat
echo if %%errorlevel%% neq 0 ( >> run_colmena.bat
echo     echo. >> run_colmena.bat
echo     echo ❌ Error al ejecutar la aplicacion >> run_colmena.bat
echo     echo Verifica que Java este instalado correctamente >> run_colmena.bat
echo     echo. >> run_colmena.bat
echo     pause >> run_colmena.bat
echo ) >> run_colmena.bat

echo ✅ Script 'run_colmena.bat' creado!
echo.
echo ARCHIVOS GENERADOS:
echo   📦 Colmena.jar          - Aplicacion ejecutable
echo   🚀 run_colmena.bat      - Script de lanzamiento
echo   📄 MANIFEST.MF          - Manifiesto del JAR
echo.
echo COMO EJECUTAR:
echo   1. java -jar Colmena.jar
echo   2. Doble clic en run_colmena.bat
echo   3. Usar Launch4j para crear Colmena.exe
echo.
echo 🎉 LISTO PARA USAR!
echo.
pause