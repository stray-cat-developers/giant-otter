#!/usr/bin/env bash
# 로컬 개발 환경(docker-compose의 mysql)은 사람에 따라 colima 또는 Docker Desktop 위에서
# 돈다. 둘 다 재부팅 후 자동으로 떠 있지 않을 수 있고(특히 colima는 Docker Desktop과 달리
# 재부팅 시 자동 기동되지 않는다), 떠 있더라도 docker 데몬 소켓이 준비되기까지 시간이
# 걸린다. 이 상태에서 바로 docker-compose up을 실행하면 데몬에 연결하지 못해 실패하고,
# 결과적으로 mysql이 뜨지 않아 애플리케이션 기동도 실패한다.
# 그래서 docker-compose up 전에 실제 사용 중인 백엔드(colima/Docker Desktop)를 판별해
# 필요하면 기동을 시도하고, docker 데몬이 응답할 때까지 대기한다.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

DOCKER_READY_TIMEOUT=180 # docker 데몬이 준비될 때까지 최대 대기 초 (Docker Desktop은 첫 기동이 느릴 수 있음)

wait_for_docker() {
	if docker info > /dev/null 2>&1; then
		echo "docker 데몬이 이미 준비되어 있습니다."
		return
	fi

	# docker context로 현재 어떤 백엔드를 쓰는지 판별해서 해당 백엔드만 기동한다.
	# (예: colima가 설치돼 있어도 실제로는 Docker Desktop을 쓰는 환경일 수 있으므로
	#  colima가 있다고 무조건 colima start를 하지 않는다.)
	local docker_context
	docker_context="$(docker context show 2> /dev/null || echo default)"

	if [ "$docker_context" = "colima" ] && command -v colima > /dev/null 2>&1; then
		echo "docker 데몬이 응답하지 않습니다. colima start로 기동합니다..."
		colima start
	elif [ -d "/Applications/Docker.app" ]; then
		echo "docker 데몬이 응답하지 않습니다. Docker Desktop을 기동합니다..."
		open -a Docker
	elif command -v colima > /dev/null 2>&1; then
		echo "docker 데몬이 응답하지 않습니다. colima start로 기동합니다..."
		colima start
	else
		echo "경고: docker 데몬이 응답하지 않고, colima/Docker Desktop을 자동으로 기동할 방법을 찾지 못했습니다. 수동으로 기동해주세요." >&2
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
