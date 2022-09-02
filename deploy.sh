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
  sudo docker stop "${CURRENT_PID}"
  sleep 5
  sudo docker rm "${CURRENT_PID}"
  sleep 5
fi

echo "> 이미지 정리 ... "
sudo docker rmi $(docker images -f "dangling=true" -qa)

echo "> 이미지 Build ..."
echo ">>> Config server 이미지 빌드"
echo ">>> Docker file 위치 - ${CONFIG_SERVER_DIR}"
cd ${CONFIG_SERVER_DIR}
sudo docker rmi ${DOCKER_USER_ID}/infra-config-server:${PREV_VERSION}
sudo docker build -t ${DOCKER_USER_ID}/infra-config-server:${APP_VERSION} .

echo ">>> Eureka server 이미지 빌드"
echo ">>> Docker file 위치 - ${EUREKA_SERVER_DIR}"
cd ${EUREKA_SERVER_DIR}
sudo docker rmi ${DOCKER_USER_ID}/infra-eureka-server:${PREV_VERSION}
sudo docker build -t ${DOCKER_USER_ID}/infra-eureka-server:${APP_VERSION} .

echo ">>> Api gateway 이미지 빌드"
echo ">>> Docker file 위치 - ${API_GATEWAY_SERVICE_DIR}"
cd ${API_GATEWAY_SERVICE_DIR}
sudo docker rmi ${DOCKER_USER_ID}/infra-api-gateway-service:${PREV_VERSION}
sudo docker build -t ${DOCKER_USER_ID}/infra-api-gateway-service:${APP_VERSION} .

echo ">>> Member service 이미지 빌드"
echo ">>> Docker file 위치 - ${MEMBER_SERVICE_DIR}"
cd ${MEMBER_SERVICE_DIR}
sudo docker rmi ${DOCKER_USER_ID}/member-service:${PREV_VERSION}
sudo docker build -t ${DOCKER_USER_ID}/member-service:${APP_VERSION} .

echo ">>> Member auth service 이미지 빌드"
echo ">>> Docker file 위치 - ${MEMBER_AUTH_SERVICE_DIR}"
cd ${MEMBER_AUTH_SERVICE_DIR}
sudo docker rmi ${DOCKER_USER_ID}/member-auth-service:${PREV_VERSION}
sudo docker build -t ${DOCKER_USER_ID}/member-auth-service:${APP_VERSION} .

echo ">>> Mentoring service 이미지 빌드"
echo ">>> Docker file 위치 - ${MENTORING_SERVICE_DIR}"
cd ${MENTORING_SERVICE_DIR}
sudo docker rmi ${DOCKER_USER_ID}/mentoring-service:${PREV_VERSION}
sudo docker build -t ${DOCKER_USER_ID}/mentoring-service:${APP_VERSION} .

echo ">>> Reply service 이미지 빌드"
echo ">>> Docker file 위치 - ${REPLY_SERVICE_DIR}"
cd ${REPLY_SERVICE_DIR}
sudo docker rmi ${DOCKER_USER_ID}/reply-service:${PREV_VERSION}
sudo docker build -t ${DOCKER_USER_ID}/reply-service:${APP_VERSION} .

echo "> Docker-compose 구동"
cd ${BASE_DIR}
docker-compose -f Mentoree-docker-compose-infra.yml up -d
sleep 180
docker-compose -f Mentoree-docker-compose-service.yml up -d