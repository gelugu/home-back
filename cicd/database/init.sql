-- create users table --
CREATE TABLE users(
	id VARCHAR(64) NOT NULL, -- unique profile id
	login VARCHAR(64) UNIQUE NOT NULL, -- profile name
	name VARCHAR(64) DEFAULT '', -- profile name
	telegram_bot_token VARCHAR(64) DEFAULT '', -- profile telegram token
	telegram_bot_chat_id VARCHAR(10) DEFAULT '', -- profile telegram chat id
	password VARCHAR(64) DEFAULT '', -- profile password
	bio TEXT DEFAULT '', -- profile description

	PRIMARY KEY(id)
);
-- add test values into users table --
INSERT INTO users(id, login, name, password)
  VALUES ('098go86fg', 'mike', 'Mikhail', 'iuLuho87Go86f8&!');

-- create tasks table --
CREATE TABLE tasks(
	id VARCHAR(64) NOT NULL, -- unique task id
	user_id VARCHAR(64) NOT NULL, -- owner id
	name VARCHAR(64) NOT NULL, -- task name
	create_date TIMESTAMP NOT NULL, -- when task was created
	description TEXT, -- task description
	open BOOLEAN, -- task status
	parent_id VARCHAR(64), -- parent task id (for subtasks)
	due_date TIMESTAMP, -- should be done to this date
	schedule_date TIMESTAMP, -- reminder to start working on

	PRIMARY KEY(id)
);
-- add test values into tasks table --
INSERT INTO tasks(id, user_id, name, create_date, description, open)
	VALUES
	('78967tuy', '098go86fg', 'Create task', CURRENT_TIMESTAMP, 'request from from front', FALSE),
	('tr6ty98a', '098go86fg', 'Update task', CURRENT_TIMESTAMP, 'back + front', FALSE),
	('o98yloja', '098go86fg', 'Delete with warning', CURRENT_TIMESTAMP, 'U do not need to delete, it can be unnecessary', TRUE),
	('o87guhka', '098go86fg', 'deploy it on servers', CURRENT_TIMESTAMP, 'yc in container', TRUE),
	('8otf6uy8', '098go86fg', 'deploy db', CURRENT_TIMESTAMP, 'connect to bucket', TRUE),
	('juoy8itw', '098go86fg', 'run k8s from scratch', CURRENT_TIMESTAMP, '', TRUE);
