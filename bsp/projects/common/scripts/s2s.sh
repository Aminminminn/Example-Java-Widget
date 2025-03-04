#!/bin/bash

set -e
set -x

S2S_TTY=${1}
S2S_PORT=${2}

if [ "$(ss -a  | grep "\<${S2S_PORT}\>")" == "" ]
then
    cd microej/MIMXRT1170-evk_platform-CM7hardfp_GCC48-2.2.0/source
    java \
        -Djava.library.path="resources/os/Linux64" \
        -cp "tools/*" \
        com.is2t.serialsockettransmitter.SerialToSocketTransmitterBatch \
        -port "${S2S_TTY}" \
        -baudrate 115200 -databits 8 -parity none -stopbits 1 \
        -hostPort "${S2S_PORT}"
fi
