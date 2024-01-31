#!/bin/bash

set_java_home_env() {
  if [ "$RUNNER_ARCH" = "ARM64" ]; then
    ARCH="arm64"
  else
    ARCH="$RUNNER_ARCH"
  fi
  JAVA_HOME_ENV="JAVA_HOME_17_${ARCH}"
}