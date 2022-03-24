DROP TABLE IF EXISTS content;
DROP TABLE IF EXISTS subscribed_to;
DROP TABLE IF EXISTS subscriber;

CREATE TABLE subscriber (
    id VARCHAR(250) PRIMARY KEY,
    user_id INT NOT NULL UNIQUE
);
CREATE TABLE subscribed_to (
    id SERIAL PRIMARY KEY,
    subscriber_id VARCHAR(250) NOT NULL,
    publisher_id VARCHAR(250) NOT NULL,
    FOREIGN KEY (subscriber_id) REFERENCES subscriber(id)
);
CREATE TABLE content (
    id SERIAL,
    publisher_id VARCHAR(250) NOT NULL,
    content VARCHAR(500) NOT NULL,
    PRIMARY KEY (id)
);