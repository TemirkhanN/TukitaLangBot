CREATE TABLE IF NOT EXISTS dictionary (
    id SERIAL PRIMARY KEY,
    word VARCHAR(255),
    translation VARCHAR(255),
    part_of_speech VARCHAR(100),
    description TEXT,
    context TEXT
);
CREATE INDEX IF NOT EXISTS idx_translation ON dictionary (translation);

CREATE TABLE IF NOT EXISTS learned_words (
    id SERIAL PRIMARY KEY,
    word_id INT,
    channel_id VARCHAR(100),
    learned_at TIMESTAMP WITHOUT TIME ZONE
);
CREATE INDEX IF NOT EXISTS idx_channel_id ON learned_words (channel_id,learned_at);

CREATE TABLE IF NOT EXISTS questions(
    id       SERIAL PRIMARY KEY,
    text     VARCHAR(255),
    answer   VARCHAR(255),
    variants TEXT
);