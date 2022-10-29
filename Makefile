version=1.0.0
container_name=gelugu/home-back:${version}

clean-db:
	rm -r .db-data/postgres || echo "No db data found"
db: clean-db
	docker-compose up

build:
	./gradlew shadowJar

docker-build:
	docker build -t ${container_name} -f cicd/Dockerfile .
docker-dev:
	docker run -p 8080:8080 -e "DB_URL=localhost" -e "DB_NAME=home-postgres-db" -e "DB_PORT=5432" -e "DB_USER=home-postgres-user" -e "DB_PASSWORD=pass" ${container_name}
docker-push:
	docker push ${container_name}
