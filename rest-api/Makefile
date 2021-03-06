SERVICE_NAME=spring-kotlin-exposed-rest-api
SERVICE_VERSION ?= $(shell test -n "$$(cat version.txt)" && cat version.txt || ./bin/version.sh | tee version.txt)

DOCKER_FILE_DB=docker/postgres/Dockerfile
DOCKER_FILE_APP=docker/app/Dockerfile

DOCKER_TAG_LOCAL_DB=local/spring-kotlin-exposed-db:latest
DOCKER_TAG_LOCAL_APP=local/$(SERVICE_NAME):$(SERVICE_VERSION)

DOCKER_COMPOSE_PLAYGROUND_FILE="docker/docker-compose-playground.yml"
DOCKER_COMPOSE_CI_FILE="docker/docker-compose-ci.yml"
DOCKER_COMPOSE_LOCAL_FILE="docker/docker-compose-local.yml"

GRADLE_CMD=../gradlew

print-%: ; @echo $*=$($*)
guard-%:
	@test ${${*}} || (echo "FAILED! Environment variable $* not set " && exit 1)
	@echo "-> use env var $* = ${${*}}";

.PHONY : help
help : Makefile
	@sed -n 's/^##//p' $<

version:
	@echo $(SERVICE_VERSION)

## db.build   : build db docker image
db.build:
	docker build -t $(DOCKER_TAG_LOCAL_DB) -f $(DOCKER_FILE_DB) .

## app.clean   : clean
app.clean:
	$(GRADLE_CMD) clean
	rm version.txt || true

## app.version   : show version
app.version:
	@echo "$(SERVICE_VERSION)"

## app.build   : clean and build (jar, docker)
app.build: app.clean app.version guard-SERVICE_VERSION db.build
	@echo "building service $(SERVICE_NAME) version $(SERVICE_VERSION)"
	mkdir -p src/main/resources/public/ && cp -rf version.txt src/main/resources/public/version.txt
	$(GRADLE_CMD) clean build test
	docker build -t $(DOCKER_TAG_LOCAL_APP) -f $(DOCKER_FILE_APP) .
	@echo "build complete: service $(SERVICE_NAME) version $(SERVICE_VERSION)"

## db-local.up   : build and start db-local
db-local.up: db.build
	docker-compose -f $(DOCKER_COMPOSE_LOCAL_FILE) up
## db-local.down   : stop db-local
db-local.down:
	docker-compose -f $(DOCKER_COMPOSE_LOCAL_FILE) down
## db-local.down.v   : stop db-local and remove volumes
db-local.down.v:
	docker-compose -f $(DOCKER_COMPOSE_LOCAL_FILE) down -v

## db-ci.up   : build and start db-ci
db-ci.up: db.build
	docker-compose -f $(DOCKER_COMPOSE_CI_FILE) up
## db-ci.down.v   : stop db-ci and remove volumes
db-ci.down.v:
	docker-compose -f $(DOCKER_COMPOSE_CI_FILE) down -v

## playground.up   : build app & db and run compose stack "playground"
playground.up: app.build playground.start
## playground.start   : start compose stack "playground"
playground.start: guard-SERVICE_VERSION
	export SERVICE_VERSION=$(SERVICE_VERSION) && export SERVICE_NAME=$(SERVICE_NAME) && docker-compose -f $(DOCKER_COMPOSE_PLAYGROUND_FILE) up
## playground.down   : stop compose stack "playground"
playground.down: guard-SERVICE_VERSION
	export SERVICE_VERSION=$(SERVICE_VERSION) && export SERVICE_NAME=$(SERVICE_NAME) && docker-compose -f $(DOCKER_COMPOSE_PLAYGROUND_FILE) down
## playground.down.v   : stop compose stack "playground" and remove volumes
playground.down.v: guard-SERVICE_VERSION
	export SERVICE_VERSION=$(SERVICE_VERSION) && export SERVICE_NAME=$(SERVICE_NAME) && docker-compose -f $(DOCKER_COMPOSE_PLAYGROUND_FILE) down -v
