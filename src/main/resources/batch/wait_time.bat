@echo off
REM filepath: src/main/resources/batch/wait_time.bat
REM バッチスクリプト：指定回数ループして待機
REM 第一引数：ループ回数
REM 第二引数：終了コード
REM 第三引数：出力先（0=標準出力、1=標準エラー）

setlocal enabledelayedexpansion

if "%1"=="" (
    echo Usage: wait_time.bat ^<loop_count^> ^<exit_code^> ^<output_type^>
    exit /b 1
)

set LOOP_COUNT=%1
set EXIT_CODE=%2
set OUTPUT_TYPE=%3

if "%EXIT_CODE%"=="" set EXIT_CODE=0
if "%OUTPUT_TYPE%"=="" set OUTPUT_TYPE=0

if %OUTPUT_TYPE% equ 0 (
    echo start wait_time
    for /l %%i in (1,1,%LOOP_COUNT%) do (
        echo loop %%i
        timeout /t 1 /nobreak > nul
    )
    echo finish wait_time
) else (
    echo start wait_time 1>&2
    for /l %%i in (1,1,%LOOP_COUNT%) do (
        echo loop %%i 1>&2
        timeout /t 1 /nobreak > nul
    )
    echo finish wait_time 1>&2
)

exit /b %EXIT_CODE%
