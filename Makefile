prepare-db:
	echo todo

db:
	docker-compose up

clean-db:
	rm -rf .db-data
