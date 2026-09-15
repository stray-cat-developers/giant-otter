#!/usr/bin/env bash
# restart-app.sh와 동일한 방식(pgrep 패턴 매칭)으로 실행 중인 애플리케이션(java 프로세스)만 정지한다.
# quick-start.sh, launchd(install-autostart.sh), restart-app.sh 중 무엇으로 띄웠든
# "java ... -jar build/libs/grantotter-*.jar" 커맨드라인은 동일하므로 이 패턴으로 찾아 정지시킨다.
set -euo pipefail

cd "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

STOP_TIMEOUT=30 # 정상 종료(SIGTERM)를 기다리는 최대 초
JAR_MATCH_PATTERN="build/libs/grantotter-.*\.jar"

PIDS=$(pgrep -f "$JAR_MATCH_PATTERN" || true)
if [ -z "$PIDS" ]; then
    echo "실행 중인 애플리케이션 프로세스가 없습니다."
    exit 0
fi

echo "애플리케이션 프로세스 정지 중 (PID: $PIDS)..."
# shellcheck disable=SC2086
kill $PIDS

waited=0
while pgrep -f "$JAR_MATCH_PATTERN" > /dev/null 2>&1; do
    if [ "$waited" -ge "$STOP_TIMEOUT" ]; then
        echo "정상 종료가 ${STOP_TIMEOUT}초 안에 끝나지 않아 강제 종료(kill -9)합니다."
        # shellcheck disable=SC2086
        kill -9 $PIDS || true
        break
    fi
    sleep 1
    waited=$((waited + 1))
done

echo "정지 완료."
