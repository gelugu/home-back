-- create profile table --
CREATE TABLE profiles(
	id VARCHAR(64) NOT NULL, -- unique profile id
	name VARCHAR(64) NOT NULL, -- profile name
	telegram_bot_token VARCHAR(64), -- profile telegram token
	telegram_bot_chat_id VARCHAR(64), -- profile telegram chat id
	bio TEXT, -- profile description
	jwt_token VARCHAR(64), -- authorization token
	jwt_token_expiration_date DATE, -- authorization token expiration date

	PRIMARY KEY(id)
);
-- add test values into profile table --
INSERT INTO profiles(id, name, bio)
  VALUES ('098go86fg', 'Mikhail', 'ground control to major tom');

-- create tasks table --
CREATE TABLE tasks(
	id VARCHAR(64) NOT NULL, -- unique task id
	name VARCHAR(64) NOT NULL, -- task name
	create_date TIMESTAMP NOT NULL, -- when task was created
	description TEXT, -- task description
	open BOOLEAN, -- task status
	parent_id VARCHAR(64), -- parent task id (for subtasks)
	due_date DATE, -- should be done to this date
	schedule_date DATE, -- reminder to start working on
	hidden BOOLEAN NOT NULL DEFAULT FALSE, -- hide task when created

	PRIMARY KEY(id)
);
-- add test values into tasks table --
INSERT INTO tasks(id, name, create_date, description, open)
	VALUES
	('78967tuy', 'Create task', CURRENT_TIMESTAMP, 'request from from front', TRUE),
	('tr6ty98a', 'Update task', CURRENT_TIMESTAMP, 'back + front', TRUE),
	('o98yloja', 'Delete with warning', CURRENT_TIMESTAMP, 'U do not need to delete, it can be unnecessary', FALSE),
	('o87guhka', 'deploy it on servers', CURRENT_TIMESTAMP, 'yc in container', TRUE),
	('8otf6uy8', 'deploy db', CURRENT_TIMESTAMP, 'connect to bucket', FALSE),
	('juoy8itw', 'run k8s from scratch', CURRENT_TIMESTAMP, '', FALSE);

-- create thoughts (aka advices) table --
CREATE TABLE thoughts(
	id VARCHAR(64) NOT NULL, -- unique thought id
	name VARCHAR(64) NOT NULL, -- thought name
	description TEXT, -- thought description

	PRIMARY KEY(id)
);
-- add test values into thoughts table --
INSERT INTO thoughts(id, name, description)
	VALUES
	('78967tuy', 'thought 1', 'description for thought 1'),
	('gi78go89', 'thought 2', 'description for thought 2'),
	('go8gp988', 'thought 3', 'description for thought 3');
