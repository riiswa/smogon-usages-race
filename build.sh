#!/bin/sh

rm -rf public/sprites/

sbt "runMain com.warisradji.smogonusagesrace.RunExtractions" && bash download_sprites.sh