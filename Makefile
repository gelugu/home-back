container_name=gelugu/home-db:0.0.1

prepare:
	echo todo

clean-db:
	rm -r .db-data/postgres
db: clean-db
	docker-compose up

docker-build:
	docker build -t ${container_name} -f cicd/Dockerfile .
docker-dev:
	docker run -p 8080:8080 -e "DB_URL=localhost" -e "DB_NAME=home-postgres-db" -e "DB_PORT=5432" -e "DB_USER=home-postgres-user" -e "DB_PASSWORD=pass" ${container_name}
docker-push:
	docker push ${container_name}
