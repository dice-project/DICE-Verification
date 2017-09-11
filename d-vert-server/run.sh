#!/bin/bash
set -e

# Script to workaround docker-machine/boot2docker OSX host volume issues: https://github.com/docker-library/mysql/issues/99

echo '* Working around permission errors locally by making sure that "celery-user" uses the same uid and gid as the host volume'
TARGET_UID=$(stat -c "%u" ${SERVER_CODE_FOLDER}static/)
echo '-- Setting celery-user user to use uid '$TARGET_UID
sudo usermod -o -u $TARGET_UID celery-user || true
TARGET_GID=$(stat -c "%g" ${SERVER_CODE_FOLDER}static/)
echo '-- Setting celery-suer group to use gid '$TARGET_GID
sudo groupmod -o -g $TARGET_GID celery-user || true
echo
echo '* Starting celery'
sudo chown -R celery-user:celery-user ${SERVER_CODE_FOLDER}static/
celery worker -l info -A run_json2mc_server.celery --without-gossip --without-mingle --without-heartbeat -Ofair
