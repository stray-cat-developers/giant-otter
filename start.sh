#!/usr/bin/env bash
# quick-start.sh는 콘솔에 붙어서(foreground) 로그를 바로 보는 디버깅용이다.
# start.sh는 그 반대로, 콘솔 연결(터미널 종료, ssh 연결 끊김 등)에 영향받지 않도록
# 처음부터 완전히 detach해서 quick-start.sh(docker-compose 기동 + 빌드 + 실행)를
# 백그라운드로 돌린다.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

usage() {
    cat <<'EOF'
사용법: ./start.sh [-log <디렉터리>] [-h]

quick-start.sh(디버깅용, 콘솔에 붙어서 실행)와 달리, 콘솔 연결에 영향받지
않도록 완전히 detach된 상태로 quick-start.sh(docker-compose 기동 + 빌드 +
애플리케이션 실행)를 백그라운드로 수행한다. 터미널을 닫아도 계속 동작한다.

옵션:
  -log <디렉터리>   지정한 디렉터리 아래에 로그 파일(start-<timestamp>.log)을
                    남긴다. 디렉터리가 없으면 새로 만든다.
                    옵션을 주지 않으면 로그를 따로 남기지 않는다(출력은 버려짐).
  -h, --help        이 도움말을 출력하고 종료한다.

예시:
  ./start.sh                     로그를 남기지 않고 detach 실행
  ./start.sh -log logs           ./logs/start-<timestamp>.log 에 로그를 남기며 detach 실행
EOF
}

LOG_DIR=""
while [ $# -gt 0 ]; do
    case "$1" in
        -log)
            if [ $# -lt 2 ]; then
                echo "오류: -log 뒤에 디렉터리 경로가 필요합니다." >&2
                usage
                exit 1
            fi
            LOG_DIR="$2"
            shift 2
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        *)
            echo "오류: 알 수 없는 옵션입니다: $1" >&2
            usage
            exit 1
            ;;
    esac
done

if [ -n "$LOG_DIR" ]; then
    mkdir -p "$LOG_DIR"
    LOG_FILE="$LOG_DIR/start-$(date +%Y%m%d-%H%M%S).log"
    echo "로그 파일: $LOG_FILE"
else
    LOG_FILE="/dev/null"
    echo "로그 옵션(-log)이 없어 별도로 로그를 남기지 않습니다."
fi

nohup "$SCRIPT_DIR/quick-start.sh" > "$LOG_FILE" 2>&1 < /dev/null &
disown

echo "detach 실행됨 (PID: $!). 콘솔을 닫아도 계속 동작합니다."
