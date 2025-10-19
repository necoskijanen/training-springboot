#!/bin/bash
# filepath: src/main/resources/batch/wait_time.sh
# シェルスクリプト：指定回数ループして待機
# 第一引数：ループ回数
# 第二引数：終了コード
# 第三引数：出力先（0=標準出力、1=標準エラー）

if [ $# -lt 1 ]; then
    echo "Usage: wait_time.sh <loop_count> [exit_code] [output_type]" >&2
    exit 1
fi

LOOP_COUNT=$1
EXIT_CODE=${2:-0}
OUTPUT_TYPE=${3:-0}

if [ "$OUTPUT_TYPE" -eq 0 ]; then
    echo "start wait_time"
    for ((i=1; i<=LOOP_COUNT; i++)); do
        echo "loop $i"
        sleep 1
    done
    echo "finish wait_time"
else
    echo "start wait_time" >&2
    for ((i=1; i<=LOOP_COUNT; i++)); do
        echo "loop $i" >&2
        sleep 1
    done
    echo "finish wait_time" >&2
fi

exit $EXIT_CODE
