DROP TABLE IF EXISTS subscriber;
CREATE TABLE subscriber (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
);
DROP TABLE IF EXISTS subscribed_to;
CREATE TABLE subscribed_to (
    id SERIAL PRIMARY KEY,
    subscriber_id INT NOT NULL,
    publisher_id INT NOT NULL,
    FOREIGN KEY (subscriber_id) REFERENCES subscriber(id)
);