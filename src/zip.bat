@echo off
setlocal EnableExtensions EnableDelayedExpansion

rem ===== 生成时间戳：YYYYMMDDHHMMSS =====
for /f %%I in ('powershell -NoProfile -Command "Get-Date -Format yyyyMMddHHmmss"') do set "TIMESTAMP=%%I"
set "ZIP_NAME=src_%TIMESTAMP%.zip"

rem ===== 可配置：排除列表（空格分隔，支持通配符与目录名）=====
rem 默认排除：.git  .idea  .vscode  *.zip（历史包）  和脚本自身
set "EXCLUDES=out samples .git .idea .vscode *.zip %~nx0 %ZIP_NAME%"

rem ===== 正文 =====
pushd "%~dp0" || (echo 進入目錄失敗 & exit /b 1)

rem 若目标包已存在（极少数重名情况）先删
if exist "%ZIP_NAME%" del /f /q "%ZIP_NAME%"

rem 优先使用內建 tar（Windows 10+ 通常有）
where tar >nul 2>nul
if %errorlevel%==0 goto use_tar

rem 无 tar 時使用 PowerShell 的 Compress-Archive
goto use_powershell

:use_tar
set "TAR_EXCLUDES="
for %%E in (%EXCLUDES%) do (
  set "TAR_EXCLUDES=!TAR_EXCLUDES! --exclude=""%%~E"""
)

rem -a 选择zip；--exclude 可多次；* 表示当前目录下所有（含子目录）
tar -a -c -f "%ZIP_NAME%" %TAR_EXCLUDES% *
if %errorlevel% neq 0 (
  echo 使用 tar 打包失敗。
  popd & exit /b 1
) else (
  echo 打包完成：%CD%\%ZIP_NAME%
  popd & exit /b 0
)

:use_powershell
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$ErrorActionPreference='Stop';" ^
  "$pwd = Get-Location();" ^
  "$zip = Join-Path $pwd '%ZIP_NAME%';" ^
  "$ex = '%EXCLUDES%'.Split(' ',[System.StringSplitOptions]::RemoveEmptyEntries);" ^
  "$all = Get-ChildItem -Recurse -Force -File;" ^
  "$toZip = $all | Where-Object { " ^
  "  $rel = $_.FullName.Substring($pwd.Path.Length).TrimStart('\'); " ^
  "  $name = $_.Name; " ^
  "  -not ($ex | Where-Object { " ^
  "     $p = $_; " ^
  "     # 若排除项是根下的目录名（如 .git/.idea/.vscode），则排除该目录及其子项 " ^
  "     if(($p -notlike '*\*') -and (Test-Path (Join-Path $pwd $p) -PathType Container)) { return $rel.StartsWith($p + '\') } " ^
  "     # 文件/通配：匹配 文件名 或 相对路径 " ^
  "     return ($name -like $p) -or ($rel -like $p) " ^
  "  }) " ^
  "}; " ^
  "Compress-Archive -Path $toZip -DestinationPath $zip -Force; " ^
  "Write-Host ('打包完成：{0}' -f $zip)"
if %errorlevel% neq 0 (
  echo 使用 PowerShell 打包失敗。
  popd & exit /b 1
)

popd
endlocal
