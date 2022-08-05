DROP TABLE IF EXISTS content;
DROP TABLE IF EXISTS subscribed_to;
DROP TABLE IF EXISTS subscriber;

CREATE TABLE subscriber (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE
);
CREATE TABLE subscribed_to (
    id SERIAL PRIMARY KEY,
    subscriber_id INT NOT NULL,
    publisher_id INT NOT NULL,
    FOREIGN KEY (subscriber_id) REFERENCES subscriber(user_id)
);
CREATE TABLE content (
    id SERIAL,
    publisher_id INT NOT NULL,
    content VARCHAR(500) NOT NULL,
    PRIMARY KEY (id)
);