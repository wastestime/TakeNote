# --------------------------------------------
# SQL script to create and populate the tables
# for the TakeNote Database (TakeNoteDB)
# Created by Thomas Nguyen
# --------------------------------------------

/*
Tables to be dropped must be listed in a logical order based on dependency.
UserFile and UserPhoto depend on User. Therefore, they must be dropped before User.
*/

DROP TABLE IF EXISTS Activity, UserFile, Notes, ContactConnections, UserPhoto, User;

/* The User table contains attributes of interest of a User. */
CREATE TABLE User
(
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    username VARCHAR (32) NOT NULL,
    password VARCHAR (32) NOT NULL,
	email VARCHAR (128) NOT NULL,      
    first_name VARCHAR (32) NOT NULL,
	last_name VARCHAR(32) NOT NULL,
	state VARCHAR (2) NOT NULL,
    security_question INT NOT NULL, /* Refers to the number of the selected security question */
    security_answer VARCHAR (128) NOT NULL,
    PRIMARY KEY (id)
);

/* Table that stores the contact lists for each user */
CREATE TABLE ContactConnections
(
	user_id INT UNSIGNED,
	contact_uid INT UNSIGNED,
	PRIMARY KEY(user_id, contact_uid), /* composite key, each contact can have multiple connections */
	FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE,
	FOREIGN KEY (contact_uid) REFERENCES User(id) ON DELETE CASCADE
);

/* The UserPhoto table contains attributes of interest of a user's photo. */
CREATE TABLE UserPhoto
(
	id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
	extension ENUM('jpeg', 'jpg', 'png', 'gif', 'bmp') NOT NULL,
	user_id INT UNSIGNED,
	FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

/* The table that stores all note records with its attributes */
CREATE TABLE Notes
(
	id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
	title VARCHAR(64) NOT NULL DEFAULT '',
	description VARCHAR(140) NOT NULL DEFAULT '',
	user_id INT UNSIGNED,
	created_time DATETIME NOT NULL, /* Save original creation datetime*/
	modified_time DATETIME NOT NULL, /* for edit datetime format 'yyyy-MM-dd HH:mm:ss', defaulted to creation time if note was just created */
	content TEXT,
	FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);
/* Table that stores user activities when a note is created, edited, or deleted*/
CREATE TABLE Activity
(
	id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
	note_id INT UNSIGNED,
	action ENUM('created', 'edited', 'deleted') NOT NULL,
	FOREIGN KEY (note_id) REFERENCES Notes(id) ON DELETE CASCADE
);
/* 
The UserFile table contains attributes of interest of a user's uploaded file. 
Note: We cannot name the table as File since it is a reserved word in MySQL.
*/
CREATE TABLE UserFile
(
	id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
	filename VARCHAR (256) NOT NULL,
	note_id INT UNSIGNED,
	FOREIGN KEY (note_id) REFERENCES Notes(id) ON DELETE CASCADE
);
