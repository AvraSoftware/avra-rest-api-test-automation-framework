#!/bin/bash

# section: export variable
export RUN_USER_UID=$(id -u)
export RUN_USER_GID=$(id -g)

export RABBITMQ_TAG_NAME="${RABBITMQ_TAG_NAME:-latest}"
export WIREMOCK_TAG_NAME="${WIREMOCK_TAG_NAME:-latest}"
export BACKEND_TAG_NAME="${BACKEND_TAG_NAME:-latest}"

export RUN_TEST_COMMAND="${RUN_TEST_COMMAND:-testNGThreads}"
export TEST_GROUP="${TEST_GROUP:-}"

NETWORK="${CI_PROJECT_NAME}-network-${CI_BUILD_ID}"

# section: nexus image with version
BACKEND_IMAGE="<nexus_addres>/<backend-image-path>:${BACKEND_TAG_NAME}"
POSTGRES="<nexus_addres>/<database-image-path>:${POSTGRES_IMAGE_NAME}"
BACKEND_RABBITMQ_IMAGE="<nexus_addres>/<rabbit-image-path>:${RABBITMQ_TAG_NAME}"
BACKEND_WIREMOCK_IMAGE="<nexus_addres>/<wiremock-image-path>:${WIREMOCK_TAG_NAME}"

# section: service container name
BACKEND_CNT="<backend-container-name>-${CI_JOB_ID}"
BACKEND_RABBITMQ_CNT="<rabbit-container-name>-${CI_JOB_ID}"
BACKEND_WIREMOCK_CNT="<wiremock-container-name>-${CI_JOB_ID}"
TEST_CNT="<rest-test-container-name>-${CI_JOB_ID}"

# section: path to env file
ENV_FILE="${CI_PROJECT_DIR}/gitlab-ci/application.env"

#  execute cleanup funnction when receives: 2-SIGINT, 3-SIGQUIT,
# 6-SIGABRT, 14-SIGALARM, 15-SIGTERM
trap 'cleanup' 2 3 6 14 15

function push_envs_to_file()
{
  # $1 -- template files location or path to files
  env | grep -vE "^_=|^TMUX_PANE=|^CI=|^USER=|^MAIL=" | while read env_var
  do
    env_name=$(echo "${env_var}" | cut -d '=' -f 1)
    env_value=$(echo "${env_var}" | cut -d '=' -f 2-)

    # skip replace if name or value of env is empty
    if [[ -z "${env_name}" || -z "${env_value}" ]]
    then
      continue
    fi

    if [[ -f "$1" ]]
    then
      replace_in_file "${env_name}" "${env_value}" "$1"
    elif [[ -d "$1" ]]
    then
      find "$1" -type f | grep -v '\.sh' | while read filename
      do
        replace_in_file "${env_name}" "${env_value}" "${filename}"
      done
    fi
  done
}

function replace_in_file()
{
  sed -i "s%$1%$2%g" $3
}

while [[ $# -gt 0 ]]
do
  key="$1"

  case $key in
    -t|--template)
    shift
    TEMPLATE_FILE="$1"
    shift
    push_envs_to_file "${TEMPLATE_FILE}"
    ;;
  esac
done

echo "#######################################################"
echo "tests from group: $TEST_GROUP"
echo "run tests command: $RUN_TEST_COMMAND"
echo "profile= gitlab-ci"
echo "backend version: $BACKEND_TAG_NAME"
echo "#######################################################"

# temporary remove volumes docker command
docker volume prune -f &>/dev/null

# temporary create by mkdir directories for backend logs
mkdir -p ${CI_PROJECT_DIR}/build_logs/<backend_name> | bash

# create docker network for services and tests
docker network create ${NETWORK} &>/dev/null

# temporary cleanup with docker command
function cleanup()
{
  echo "Cleaning up..."
  docker ps -a | grep '.*<backend_name>-*.' | grep "-${CI_JOB_ID}" | awk '{print $1}' | xargs docker rm -fv &>/dev/null
  docker network rm ${NETWORK} &>/dev/null
}

# section: docker pull and run services with containers
# temporary run containers with docker command
docker pull ${BACKEND_RABBITMQ_IMAGE} &>/dev/null
docker pull ${BACKEND_WIREMOCK_IMAGE} &>/dev/null
docker run --network ${NETWORK} --name ${BACKEND_RABBITMQ_CNT} -d ${BACKEND_RABBITMQ_IMAGE} &>/dev/null
docker run --network ${NETWORK} --name ${BACKEND_WIREMOCK_CNT} -d ${BACKEND_WIREMOCK_IMAGE} &>/dev/null

echo "#######################################################"
echo "sleep for services"
echo "#######################################################"
sleep 10

# temporary run 2 containers with docker command
docker pull ${BACKEND_IMAGE} &>/dev/null
docker run --network ${NETWORK} --name ${BACKEND_CNT} --mount type=bind,source=${CI_PROJECT_DIR}/build_logs/<backend_name>,target=/logs -d --env-file ${ENV_FILE} ${BACKEND_IMAGE} &>/dev/null

echo "#######################################################"
echo "sleep for backend instances"
echo "#######################################################"
sleep 30

# temporary check running containers with docker command
docker ps -a

echo "#######################################################"
echo "run the tests"
echo "#######################################################"
# run the tests
docker run --rm -i -v `pwd`:/tests --network ${NETWORK} --name ${TEST_CNT} -e TZ=Europe/Warsaw -u root ${GRADLE_IMAGE} sh -c "usermod -u $(id -u) gradle ; su -c 'cd /tests ; gradle --no-daemon ${RUN_TEST_COMMAND} -Pprofile=gitlab-ci -Dgroups=${TEST_GROUP}' gradle"
exit_code=$?

echo "#######################################################"
echo "cleanup"
echo "#######################################################"
cleanup

echo "#######################################################"
echo "sleep for cleanup"
echo "#######################################################"
sleep 5
exit $exit_code
