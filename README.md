# autorest4db

automatically generate a RESTful API of your database (sqlite3 or mysql) in runtime.

## QuickStart

###  Install and start

#### for any platform

download [autorest4db-0.1.1.tar.gz](https://github.com/downgoon/autorest4db/releases/download/0.1.1/autorest4db-0.1.1.tar.gz) and decompress it

``` bash
$ wget https://github.com/downgoon/autorest4db/releases/download/0.1.1/autorest4db-0.1.1.tar.gz
$ tar zxvf autorest4db-0.1.1.tar.gz
$ cd autorest4db-0.1.1
$ bin/autorest4db start
```

in IDE (such as Eclipse), just run ``AutoRestMain.java``

BTW, how to build and package ?

``` bash
git clone https://github.com/downgoon/autorest4db.git
cd autorest4db
mvn clean package
```

``autorest4db-0.1.1.tar.gz`` will be generated in the ``target`` directory.


>REQUIREMENT
> - JDK8 +

#### for Mac platform (64bit)

- download [autorest4db-0.1.1.pkg](https://github.com/downgoon/autorest4db/releases/download/0.1.1/autorest4db-0.1.1.pkg)
- double click it and install
- ``autorest4db start``

![autorest4db install package on Mac](https://cloud.githubusercontent.com/assets/23731186/25697137/58f7bfbc-30ec-11e7-8afd-4db334227efa.png)

---

### Create dababase and tables

create a database named ``default`` and several tables defined in ``default.sql`` script located in the root directory of the project just by uploading the script like as follows:

``` java
$ curl -F "fname=@default.sql" http://127.0.0.1:8080/dbmeta/default
{"fname":"SUCC"}%
```
BTW, multi scripts can be supported.

contents of the sql script:

``` sql

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

```

### CRUD operations

- **list entity**

get list of ``employee`` from database ``default``

``` bash
$ curl -i -X GET http://localhost:8080/dbapi/default/employee
HTTP/1.1 200 OK
Server: autorest4db
Content-Type: application/json;charset=UTF-8
Content-Length: 302

{"resources":[{"id":2,"name":"laoer","age":30,"credit":null},{"id":3,"name":"tong ye","age":48,"credit":null},{"id":4,"name":"zhangyi","age":36,"credit":null},{"id":5,"name":"陈六子","age":38,"credit":null},{"id":6,"name":"fds","age":4,"credit":null},{"id":7,"name":"timer","age":12,"credit":null}]}%
```

- **view entity**

get detail info of ``employee`` whose ``id`` is 4 from database ``default``

``` bash
$ curl -i -X GET http://localhost:8080/dbapi/default/employee/4
HTTP/1.1 200 OK
Server: autorest4db
Content-Type: application/json;charset=UTF-8
Content-Length: 48

{"id":4,"name":"zhangyi","age":36,"credit":null}
```

- **filter entity**

get detail info of ``employee`` whose ``name`` is 'zhangyi' from database ``default``

``` bash
$ curl -i -X GET "http://localhost:8080/dbapi/default/employee?name=zhangyi"
HTTP/1.1 200 OK
Server: autorest4db
Content-Type: application/json;charset=UTF-8
Content-Length: 64

{"resources":[{"id":4,"name":"zhangyi","age":36,"credit":null}]}%
```

- **create entity**

create an employee specified id=99

``` bash
$ curl -X POST -i -d '{"id": 99, "name": "wangyi", "age": 28, "credit": 9.3 }' -H "Content-Type: application/json" http://localhost:8080/dbapi/default/employee
HTTP/1.1 200 OK
Server: autorest4db
Content-Type: application/json;charset=UTF-8
Content-Length: 47

{"id":99,"name":"wangyi","age":28,"credit":9.3}%
```

create an employee without id

``` bash
$ curl -X POST -i -d '{"name": "laoer", "age": 30 }' -H "Content-Type: application/json" http://localhost:8080/dbapi/default/employee
HTTP/1.1 200 OK
Server: autorest4db
Content-Type: application/json;charset=UTF-8
Content-Length: 48

{"id":100,"name":"laoer","age":30,"credit":null}
```

- **update entity**

update employee=99

``` bash
$ curl -X PUT -i -d '{"name": "wangyiV2", "age": 32 }' -H "Content-Type: application/json" http://localhost:8080/dbapi/default/employee/99
HTTP/1.1 200 OK
Server: autorest4db
Content-Type: application/json;charset=UTF-8
Content-Length: 28

{"name":"wangyiV2","age":32}

$ curl -X GET http://localhost:8080/dbapi/default/employee/99
{"id":99,"name":"wangyiV2","age":32,"credit":9.3}%

```

- **delete entity**

``` bash
$ curl -i -X DELETE http://localhost:8080/dbapi/default/employee/99
HTTP/1.1 200 OK
Server: autorest4db
Content-Type: application/json;charset=UTF-8
Content-Length: 0

$ curl -X GET http://localhost:8080/dbapi/default/employee/99
Not Found%
```

- **composite keys**

``` bash
$ curl -i -X GET http://localhost:8080/dbapi/default/friend
HTTP/1.1 200 OK
Server: autorest4db
Content-Type: application/json;charset=UTF-8
Content-Length: 144

{"resources":[{"user_id":1001,"frie_id":1032,"degree":4},{"user_id":1001,"frie_id":1033,"degree":2},{"user_id":1002,"frie_id":1032,"degree":6}]}%

$ curl -i -X GET http://localhost:8080/dbapi/default/friend/1001-1032
HTTP/1.1 200 OK
Server: autorest4db
Content-Type: application/json;charset=UTF-8
Content-Length: 42

{"user_id":1001,"frie_id":1032,"degree":4}%
```

the composite key value '1001-1032' would be split into '1001' assigned to 'user_id' and '1032' assigned to 'frie_id'.
