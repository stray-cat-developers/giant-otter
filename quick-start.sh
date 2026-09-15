#!/usr/bin/env bash
# 로컬 개발 환경(docker-compose의 mysql)이 colima 위에서 돈다. colima는 Docker Desktop과
# 달리 재부팅 시 자동으로 기동되지 않고, 기동되어 있어도 docker 데몬 소켓이 준비되기까지
# 시간이 걸린다. 이 상태에서 바로 docker-compose up을 실행하면 데몬에 연결하지 못해
# 실패하고, 결과적으로 mysql이 뜨지 않아 애플리케이션 기동도 실패한다.
# 그래서 docker-compose up 전에 colima를 기동하고 docker 데몬이 응답할 때까지 대기한다.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

DOCKER_READY_TIMEOUT=120 # colima/docker 데몬이 준비될 때까지 최대 대기 초

wait_for_docker() {
	if command -v colima > /dev/null 2>&1 && ! colima status > /dev/null 2>&1; then
		echo "colima가 실행 중이 아닙니다. colima start로 기동합니다..."
		colima start
	fi

	echo "docker 데몬이 준비될 때까지 대기 중..."
	local waited=0
	until docker info > /dev/null 2>&1; do
		if [ "$waited" -ge "$DOCKER_READY_TIMEOUT" ]; then
			echo "오류: ${DOCKER_READY_TIMEOUT}초 안에 docker 데몬이 준비되지 않았습니다." >&2
			exit 1
		fi
		sleep 2
		waited=$((waited + 2))
	done
	echo "docker 데몬 준비 완료."
}

wait_for_docker

docker-compose up &

VERSION=$(./gradlew version -q)

./gradlew clean

if [ ! -f build/libs/grantotter-"$VERSION".jar ]; then
	./gradlew build -x test
fi

java \
	-XX:MaxMetaspaceSize=100m \
	-Xmx512m \
	-jar build/libs/grantotter-"$VERSION".jar
