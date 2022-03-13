CREATE TABLE subscriber (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE subscribed_to (
    id SERIAL PRIMARY KEY,
    subscriber_id INT NOT NULL,
    publisher_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (subscriber_id) REFERENCES subscriber(id)
);