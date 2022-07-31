-- create tasks table --
CREATE TABLE tasks(
	id VARCHAR NOT NULL,
	name VARCHAR(64) NOT NULL,
	description TEXT,
	open BOOLEAN,

	PRIMARY KEY(id)
);

-- add test values into tasks table --
INSERT INTO tasks(id, name, description, open)
	VALUES
	('78967tuy', 'task 1', 'description for task 1', TRUE),
	('tr6ty98a', 'task 2', 'description for task 2', TRUE),
	('o98yloja', 'task 3', 'description for task 3', FALSE),
	('o87guhka', 'task 4', 'description for task 4', TRUE),
	('juoy8itw', 'task 5', 'description for task 5', FALSE);