#!/usr/bin/env bash
# quick-start.sh와 달리 docker/gradle build는 건드리지 않고, 이미 빌드된 jar로
# 애플리케이션(java 프로세스)만 정지 후 재시작한다.
#
# 코드가 바뀐 뒤라면 이 스크립트를 돌리기 전에 먼저 jar를 새로 만들어야 한다.
#   ./gradlew build -x test   (또는 ./quick-start.sh)
set -euo pipefail

cd "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

PORT=6200
STOP_TIMEOUT=30 # 정상 종료(SIGTERM)를 기다리는 최대 초
JAR_MATCH_PATTERN="build/libs/grantotter-.*\.jar"

VERSION=$(grep -E '^version = "' build.gradle.kts | sed -E 's/^version = "(.*)"$/\1/')
JAR="build/libs/grantotter-${VERSION}.jar"

if [ ! -f "$JAR" ]; then
    echo "오류: $JAR 가 없습니다. 먼저 ./quick-start.sh (또는 ./gradlew build -x test)로 빌드하세요." >&2
    exit 1
fi

# 1) 기존 프로세스 정지 (quick-start.sh, launchd, 혹은 이 스크립트로 띄운 것 모두
#    "java ... -jar build/libs/grantotter-*.jar" 커맨드라인이므로 패턴 매칭으로 찾는다)
PIDS=$(pgrep -f "$JAR_MATCH_PATTERN" || true)
if [ -n "$PIDS" ]; then
    echo "기존 애플리케이션 프로세스 정지 중 (PID: $PIDS)..."
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
else
    echo "실행 중인 애플리케이션 프로세스가 없습니다. 바로 시작합니다."
fi

# 2) 포트가 실제로 풀릴 때까지 대기 (최대 15초)
waited=0
while lsof -ti "tcp:${PORT}" > /dev/null 2>&1; do
    if [ "$waited" -ge 15 ]; then
        echo "경고: ${PORT} 포트를 다른 프로세스가 아직 점유 중입니다. 그대로 시작을 시도합니다." >&2
        break
    fi
    sleep 1
    waited=$((waited + 1))
done

# 3) 재시작
mkdir -p logs
LOG_FILE="logs/app.log"
echo "애플리케이션 시작 중... ($JAR)"
nohup java \
    -XX:MaxMetaspaceSize=300m \
    -Xms512m \
    -Xmx1024m \
    -jar "$JAR" \
    >> "$LOG_FILE" 2>&1 &
disown

NEW_PID=$!
sleep 1
if kill -0 "$NEW_PID" 2>/dev/null; then
    echo "재시작 완료 (PID: $NEW_PID). 로그: $LOG_FILE (tail -f $LOG_FILE)"
else
    echo "오류: 애플리케이션이 시작 직후 종료되었습니다. $LOG_FILE 을 확인하세요." >&2
    exit 1
fi
