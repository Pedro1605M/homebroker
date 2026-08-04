# Script para baixar as bibliotecas JavaFX para a pasta lib
$libDir = "lib"

$urls = @(
    "https://repo1.maven.org/maven2/org/openjfx/javafx-base/17.0.10/javafx-base-17.0.10.jar",
    "https://repo1.maven.org/maven2/org/openjfx/javafx-base/17.0.10/javafx-base-17.0.10-win.jar",
    "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/17.0.10/javafx-graphics-17.0.10.jar",
    "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/17.0.10/javafx-graphics-17.0.10-win.jar",
    "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/17.0.10/javafx-controls-17.0.10.jar",
    "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/17.0.10/javafx-controls-17.0.10-win.jar",
    "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/17.0.10/javafx-fxml-17.0.10.jar",
    "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/17.0.10/javafx-fxml-17.0.10-win.jar"
)

foreach ($url in $urls) {
    $fileName = Split-Path $url -Leaf
    $outputPath = Join-Path $libDir $fileName
    if (-not (Test-Path $outputPath)) {
        Write-Host "Baixando $fileName..."
        Invoke-WebRequest -Uri $url -OutFile $outputPath
    } else {
        Write-Host "$fileName ja existe."
    }
}
Write-Host "Download concluido com sucesso!"
