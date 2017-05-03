PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;


-- employee table:  AUTOINCREMENT 
 
CREATE TABLE `employee` (
    `id`    INTEGER PRIMARY KEY AUTOINCREMENT,
    `name`    TEXT NOT NULL,
    `age`    INTEGER,
    `credit` NUMERIC
    );

INSERT INTO "employee" VALUES(2,'laoer',30,NULL);
INSERT INTO "employee" VALUES(3,'tong ye',48,NULL);
INSERT INTO "employee" VALUES(4,'zhangyi',36,NULL);
INSERT INTO "employee" VALUES(5,'陈六子',38,NULL);
INSERT INTO "employee" VALUES(6,'fds',4,NULL);
INSERT INTO "employee" VALUES(7,'timer',12,NULL);
    

-- student table: irregular naming

CREATE TABLE `student` (
    `stu_id`    INTEGER PRIMARY KEY,
    `stu_name`    TEXT NOT NULL,
    `stu_age`    INTEGER
    );


-- friend table: composite keys

CREATE TABLE `FRIEND` (
    `USER_id`   INTEGER NOT NULL,
    `FRIE_id`   INTEGER NOT NULL,
    `degree`    INTEGER,
    primary key ( `USER_id`, `FRIE_id` )
);

INSERT INTO FRIEND VALUES (1001, 1032, 4);
INSERT INTO FRIEND VALUES (1001, 1033, 2);
INSERT INTO FRIEND VALUES (1002, 1032, 6);
   
COMMIT;
