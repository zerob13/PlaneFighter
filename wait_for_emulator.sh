#########################################################################
# File Name: wait_for_emulator.sh
# Author: zerob13
# mail: zerob13@gmail.com
# Created Time: Sun 02 Mar 2014 01:52:35 AM CST
#########################################################################
#!/bin/bash
bootanim=""
failcounter=0
until [[ "$bootanim" =~ "stopped" ]]; do
   bootanim=`adb -e shell getprop init.svc.bootanim 2>&1`
   echo "$bootanim"
   if [[ "$bootanim" =~ "not found" ]]; then
      let "failcounter += 1"
      if [[ $failcounter -gt 3 ]]; then
        echo "Failed to start emulator"
        exit 1
      fi
   fi
   sleep 1
done
echo "Done"

