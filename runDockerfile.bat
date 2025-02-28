@echo off
docker run --rm --volume ./build/executable:/home/build/workspace/build/executable --mac-address="C8:4B:D6:5C:C0:07" --name="build" examplejavawidget
docker container stop build
docker container rm build
