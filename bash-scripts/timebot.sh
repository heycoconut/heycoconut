#!/bin/bash

ENDPOINT_URL='https://heycoconut.herokuapp.com/health'
ACTIVE_TIME_START="6:00"
ACTIVE_TIME_STOP="23:00"
PING_INTERVAL=1200

# MATH: 24h - stop time - start time = hours to sleep * 3600 = seconds to sleep
# couldn't convert string to date somehow...
SLEEP_TIME="$((18 * 3600))"

echo "###############################################"
echo "STARTING THE TIMEBOT!"
echo "PING_INTERVAL: ${PING_INTERVAL}"
echo "Active start time: $ACTIVE_TIME_START"
echo "Active stop time: $ACTIVE_TIME_STOP"
echo "Will sleep for $SLEEP_TIME seconds, when not in the active hours"
currenttime=$(date +%H:%M)
echo "It's currently $currenttime"
echo "###############################################"

{
  while :; do
   currenttime=$(date +%H:%M)
   if [[ "$currenttime" > "$ACTIVE_TIME_START" ]] || [[ "$currenttime" < "$ACTIVE_TIME_STOP" ]]; then
     echo "[$(date)]"; curl $ENDPOINT_URL; echo ""
     sleep $PING_INTERVAL
   else
     echo "It's sleeping time! I will sleep for ${SLEEP_TIME} seconds"
     sleep $SLEEP_TIME
   fi
  done
}
