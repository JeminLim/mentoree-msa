#!/bin/bash

DOCKER_USER_ID=wer080
APP_VERSION=1.0

CONFIG_SERVER_DIR=~/infra/config-server/
EUREKA_SERVER_DIR=~/infra/eureka-server/
API_GATEWAY_SERVICE_DIR=~/infra/api-gateway-service/

MEMBER_SERVICE_DIR=~/member/app-api/
MEMBER_AUTH_SERVICE_DIR=~/member/app-auth-server/
MENTORING_SERVICE_DIR=~/mentoring/app-api/
REPLY_SERVICE_DIR=~/reply/app-api/

echo "> 현재 실행 중인 Docker 컨테이너 Pid 확인"
CURRENT_PID=$(sudo docker container ls -q)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 구동 중인 컨테이너가 없습니다."
else
  echo "> 현재 구동 중인 컨테이너를 종료합니다. PID = $CURRENT_PID "
  sudo docker stop $CURRENT_PID
  sleep 5
fi

echo "> 이미지 파일 빌드 ... "

echo ">>> Config server 이미지 빌드"
cd CONFIG_SERVER_DIR
docker build -t $DOCKER_USER_ID/infra-config-server:$APP_VERSION

echo ">>> Eureka server 이미지 빌드"
cd CONFIG_SERVER_DIR
docker build -t $DOCKER_USER_ID/infra-config-server:$APP_VERSION

echo ">>> Api gateway 이미지 빌드"
cd CONFIG_SERVER_DIR
docker build -t $DOCKER_USER_ID/infra-config-server:$APP_VERSION

echo ">>> Member service 이미지 빌드"
cd CONFIG_SERVER_DIR
docker build -t $DOCKER_USER_ID/infra-config-server:$APP_VERSION

echo ">>> Member auth service 이미지 빌드"
cd CONFIG_SERVER_DIR
docker build -t $DOCKER_USER_ID/infra-config-server:$APP_VERSION

echo ">>> Mentoring service 이미지 빌드"
cd CONFIG_SERVER_DIR
docker build -t $DOCKER_USER_ID/infra-config-server:$APP_VERSION

echo ">>> Reply service 이미지 빌드"
cd CONFIG_SERVER_DIR
docker build -t $DOCKER_USER_ID/infra-config-server:$APP_VERSION

echo "> Docker-compose 구동"
cd ~
docker-compose -f Mentoree-docker-compose.yml up -d