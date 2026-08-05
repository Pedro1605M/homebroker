@echo off
echo Compilando...
javac --module-path lib --add-modules javafx.controls,javafx.fxml -cp "lib\*" -d bin src\*.java
if %errorlevel% neq 0 (
    echo ERRO na compilacao! Verifique os erros acima.
    pause
    exit /b 1
)

echo Copiando recursos...
copy /Y src\*.fxml bin\ >nul

echo Rodando o app...
java --module-path lib --add-modules javafx.controls,javafx.fxml -cp bin Main
