@echo off
setlocal

where gcc >nul 2>&1
if errorlevel 1 (
  echo gcc not found. Install MinGW-w64 or MSYS2, then re-run build.bat
  exit /b 1
)

if not exist build mkdir build
gcc -std=c11 -Wall -Wextra -Wpedantic -O2 -Iinclude src\main.c src\check.c -o build\inspection-check.exe
if errorlevel 1 exit /b 1
echo Built build\inspection-check.exe

if /I "%~1"=="test" (
  gcc -std=c11 -Wall -Wextra -Wpedantic -O2 -Iinclude tests\test_check.c src\check.c -o build\test_check.exe
  if errorlevel 1 exit /b 1
  build\test_check.exe
  if errorlevel 1 exit /b 1
  build\inspection-check.exe --min 0.48 --max 0.52 --file samples\measures.txt
  if errorlevel 1 exit /b 1
) else (
  echo Example:
  echo   build\inspection-check.exe --min 0.48 --max 0.52 --file samples\measures.txt
  echo Test:
  echo   build.bat test
)
endlocal
