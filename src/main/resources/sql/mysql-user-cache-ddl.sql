CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO users (name, age) VALUES ('kirri', 20);
INSERT INTO users (name, age) VALUES ('redis-user', 25);
