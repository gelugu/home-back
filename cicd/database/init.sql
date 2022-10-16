CREATE TABLE users(
	id VARCHAR(64) NOT NULL PRIMARY KEY,
	login VARCHAR(64) UNIQUE NOT NULL,
	name VARCHAR(64) DEFAULT '',
	telegram_bot_token VARCHAR(64) DEFAULT '',
	telegram_bot_chat_id VARCHAR(10) DEFAULT '',
	password VARCHAR(64) DEFAULT '',
	bio TEXT DEFAULT ''
);
INSERT INTO users(id, login, name, password)
  VALUES ('098go86fg', 'mike', 'Mikhail', 'iuLuho87Go86f8&!');

CREATE TABLE tracks(
	id VARCHAR(64) NOT NULL PRIMARY KEY,
	name VARCHAR(64) DEFAULT '',
	description TEXT DEFAULT '',
	owner VARCHAR(64) REFERENCES users(id)
);
INSERT INTO tracks(id, name, owner)
  VALUES ('default', 'default', '098go86fg');

CREATE TABLE tasks(
	id VARCHAR(64) NOT NULL PRIMARY KEY,
	track_id VARCHAR(64) REFERENCES tracks(id),
	name VARCHAR(64) NOT NULL,
	create_date TIMESTAMP NOT NULL,
	description TEXT,
	open BOOLEAN,
	parent_id VARCHAR(64),
	due_date TIMESTAMP,
	schedule_date TIMESTAMP
);
INSERT INTO tasks(id, name, create_date, description, open, track_id)
	VALUES
	('78967tuy', 'Create task', CURRENT_TIMESTAMP, 'request from from front', FALSE, 'default'),
	('tr6ty98a', 'Update task', CURRENT_TIMESTAMP, 'back + front', FALSE, 'default'),
	('o98yloja', 'Delete with warning', CURRENT_TIMESTAMP, 'U do not need to delete, it can be unnecessary', TRUE, 'default'),
	('o87guhka', 'deploy it on servers', CURRENT_TIMESTAMP, 'yc in container', TRUE, 'default'),
	('8otf6uy8', 'deploy db', CURRENT_TIMESTAMP, 'connect to bucket', TRUE, 'default'),
	('juoy8itw', 'run k8s from scratch', CURRENT_TIMESTAMP, '', TRUE, 'default');
