@echo off
cd /d "%~dp0"
echo Compilando o jogo...
javac -encoding UTF-8 SudokuBinario.java
if errorlevel 1 (
  echo.
  echo Erro na compilacao. Verifique se o JDK esta instalado e no PATH.
  pause
  exit /b 1
)
echo.
echo Iniciando Sudoku Binario...
java SudokuBinario
echo.
pause