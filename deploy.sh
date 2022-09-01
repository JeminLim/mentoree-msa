#!/bin/bash

DOCKER_USER_ID="wer080"
PREV_VERSION="1.0"
APP_VERSION="2.0"

BASE_DIR="/home/ec2-user/app/mentoree-webservice/zip"

CONFIG_SERVER_DIR="${BASE_DIR}/infra/config-server/"
EUREKA_SERVER_DIR="${BASE_DIR}/infra/eureka-server/"
API_GATEWAY_SERVICE_DIR="${BASE_DIR}/infra/api-gateway-service/"

MEMBER_SERVICE_DIR="${BASE_DIR}/member/app-api/"
MEMBER_AUTH_SERVICE_DIR="${BASE_DIR}/member/app-auth-server/"
MENTORING_SERVICE_DIR="${BASE_DIR}/mentoring/app-api/"
REPLY_SERVICE_DIR="${BASE_DIR}/reply/app-api/"

echo "> 현재 실행 중인 Docker 컨테이너 Pid 확인"
CURRENT_PID=$(docker container ls -qa)

if [ -z "${CURRENT_PID}" ]
then
  echo "> 현재 구동 중인 컨테이너가 없습니다."
else
  echo "> 현재 구동 중인 컨테이너를 종료합니다. PID = ${CURRENT_PID} "
  docker stop "${CURRENT_PID}"
  sleep 5
  docker rm "${CURRENT_PID}"
  sleep 5
fi

echo "> 이미지 파일 빌드 ... "
echo ">>> Config server 이미지 빌드"
docker rmi ${DOCKER_USER_ID}/infra-config-server:${PREV_VERSION}
docker build -f ${CONFIG_SERVER_DIR}/Dockerfile -t ${DOCKER_USER_ID}/infra-config-server:${APP_VERSION} .

echo ">>> Eureka server 이미지 빌드"
docker rmi ${DOCKER_USER_ID}/infra-eureka-server:${PREV_VERSION}
docker build -f ${EUREKA_SERVER_DIR}/Dockerfile -t ${DOCKER_USER_ID}/infra-eureka-server:${APP_VERSION} .

echo ">>> Api gateway 이미지 빌드"
docker rmi ${DOCKER_USER_ID}/infra-api-gateway-servic:${PREV_VERSION}
docker build -f ${API_GATEWAY_SERVICE_DIR}/Dockerfile -t ${DOCKER_USER_ID}/infra-api-gateway-servic:${APP_VERSION} .

echo ">>> Member service 이미지 빌드"
docker rmi ${DOCKER_USER_ID}/member-service:${PREV_VERSION}
docker build -f ${MEMBER_SERVICE_DIR}/Dockerfile -t ${DOCKER_USER_ID}/member-service:${APP_VERSION} .

echo ">>> Member auth service 이미지 빌드"
docker rmi ${DOCKER_USER_ID}/member-auth-service:${PREV_VERSION}
docker build -f ${MEMBER_AUTH_SERVICE_DIR}/Dockerfile -t ${DOCKER_USER_ID}/member-auth-service:${APP_VERSION} .

echo ">>> Mentoring service 이미지 빌드"
docker rmi ${DOCKER_USER_ID}/mentoring-service:${PREV_VERSION}
docker build -f ${MENTORING_SERVICE_DIR}/Dockerfile -t ${DOCKER_USER_ID}/mentoring-service:${APP_VERSION} .

echo ">>> Reply service 이미지 빌드"
docker rmi ${DOCKER_USER_ID}/reply-service:${PREV_VERSION}
docker build -f ${REPLY_SERVICE_DIR}/Dockerfile -t ${DOCKER_USER_ID}/reply-service:${APP_VERSION} .

echo "> Docker-compose 구동"
cd ~
docker-compose -f ${BASE_DIR}/Mentoree-docker-compose-infra.yml up -d
sleep 180
docker-compose -f ${BASE_DIR}/Mentoree-docker-compose-service.yml up -d