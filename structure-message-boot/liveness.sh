#!/bin/sh

PORT="7777"
islive () {
health=`/usr/bin/wget -O - -q http://localhost:${PORT}/actuator/health  | grep  -B 1 '"status"' |  awk -F '"'  '{print $4}'`

if [[ "${health}"x == "UP"x  ]]; then
echo "health success"
exit 0
else
echo "health err"
exit 1
fi
}
islive