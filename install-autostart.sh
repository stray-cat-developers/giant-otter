#!/usr/bin/env bash
# macOS 로그인/재부팅 시 quick-start.sh(docker + build + run)가 자동으로 실행되도록
# launchd에 사용자 LaunchAgent를 등록한다.
#
# LaunchDaemon(부팅 시 로그인 없이 실행)이 아니라 LaunchAgent(로그인 시 실행)를 쓰는 이유:
# quick-start.sh가 띄우는 docker-compose(mysql)는 colima가 있어야 동작하는데, colima는
# 사용자 홈 디렉터리(lima VM 설정 등)를 사용하므로 사용자가 로그인한 세션에서만 정상
# 동작한다. root로 로그인 전에 도는 LaunchDaemon으로 등록하면 colima 자체가 없어서 실패한다.
# (colima는 Docker Desktop과 달리 재부팅 후 자동으로 기동되지 않고, 기동되어도 docker
# 데몬 소켓이 뜨기까지 시간이 걸린다. quick-start.sh는 이 점을 감안해 docker-compose up 전에
# colima를 기동하고 데몬이 준비될 때까지 대기한다.)
#
# 크래시 시 자동 재시작(KeepAlive)은 켜지 않았다: 이미 떠 있는 애플리케이션을 정지시키는
# ./restart-app.sh 와 동시에 쓰면, restart-app.sh가 kill하자마자 launchd가 다시 띄워버려서
# 서로 경쟁하게 된다. 그래서 "로그인/재부팅 시 1회 자동 실행"만 담당하고, 이미 떠 있는 걸
# 내리고 다시 올리는 건 restart-app.sh가 전담한다.
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LABEL="io.mustelidae.grantotter"
PLIST_PATH="$HOME/Library/LaunchAgents/${LABEL}.plist"
LOG_DIR="$PROJECT_DIR/logs"
UID_GUI="gui/$(id -u)"

if [ "$(uname)" != "Darwin" ]; then
    echo "오류: 이 스크립트는 macOS(launchd) 전용입니다." >&2
    exit 1
fi

mkdir -p "$LOG_DIR" "$HOME/Library/LaunchAgents"

cat > "$PLIST_PATH" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>Label</key>
    <string>${LABEL}</string>
    <key>ProgramArguments</key>
    <array>
        <string>/bin/bash</string>
        <string>-lc</string>
        <string>exec ${PROJECT_DIR}/quick-start.sh</string>
    </array>
    <key>WorkingDirectory</key>
    <string>${PROJECT_DIR}</string>
    <key>RunAtLoad</key>
    <true/>
    <key>StandardOutPath</key>
    <string>${LOG_DIR}/autostart.log</string>
    <key>StandardErrorPath</key>
    <string>${LOG_DIR}/autostart.log</string>
</dict>
</plist>
EOF

echo "LaunchAgent plist 작성: $PLIST_PATH"

# 이미 등록되어 있으면 내렸다가 새 설정으로 다시 올린다.
launchctl bootout "$UID_GUI" "$PLIST_PATH" > /dev/null 2>&1 || true
launchctl bootstrap "$UID_GUI" "$PLIST_PATH"
launchctl enable "${UID_GUI}/${LABEL}"

cat <<EOF

등록 완료. 다음 로그인/재부팅부터 quick-start.sh가 자동으로 실행됩니다.
(방금 launchctl bootstrap으로 등록과 동시에 지금 바로 한 번 실행되었습니다 - RunAtLoad)

  - 상태 확인      : launchctl print ${UID_GUI}/${LABEL}
  - 로그 확인       : tail -f ${LOG_DIR}/autostart.log
  - 지금 다시 실행   : launchctl kickstart -k ${UID_GUI}/${LABEL}
  - 애플리케이션만 재시작(자동실행 유지한 채): ./restart-app.sh
  - 자동 실행 해제   : launchctl bootout ${UID_GUI}/${LABEL} && rm ${PLIST_PATH}
EOF
