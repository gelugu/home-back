container_name=gelugu/home-back:0.0.1
terraform_dir=cicd/terraform

prepare:
	echo todo

clean-db:
	rm -r .db-data/postgres || echo "No db data found"
db: clean-db
	docker-compose up home-postgres pgadmin-home

docker-build:
	docker build -t ${container_name} -f cicd/Dockerfile .
docker-dev:
	docker run -p 8080:8080 -e "DB_URL=localhost" -e "DB_NAME=home-postgres-db" -e "DB_PORT=5432" -e "DB_USER=home-postgres-user" -e "DB_PASSWORD=pass" ${container_name}
docker-push:
	docker push ${container_name}

terraform-init:
	terraform -chdir=${terraform_dir} init

plan:
	terraform -chdir=${terraform_dir} plan

apply:
	terraform -chdir=${terraform_dir} apply

destroy:
	terraform -chdir=${terraform_dir} destroy
