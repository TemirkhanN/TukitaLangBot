CREATE TABLE dictionary (
    id SERIAL PRIMARY KEY,
    word VARCHAR(255),
    translation VARCHAR(255),
    part_of_speech VARCHAR(100),
    description TEXT,
    context TEXT
);
CREATE INDEX idx_translation ON dictionary (translation);

CREATE TABLE learned_words (
    id SERIAL PRIMARY KEY,
    word_id INT,
    channel_id VARCHAR(100),
    learned_at TIMESTAMP WITHOUT TIME ZONE
);
CREATE INDEX idx_channel_id ON learned_words (channel_id,learned_at);

CREATE TABLE questions(
    id       SERIAL PRIMARY KEY,
    text     VARCHAR(255),
    answer   VARCHAR(255),
    variants TEXT
);

CREATE TABLE ch_questions (
    id UUID PRIMARY KEY,
    question_id INT,
    channel_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ch_question_replies (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(32),
    channel_id VARCHAR(100),
    question_id UUID,
    is_correct BOOLEAN,
    CONSTRAINT unique_channel_user_question_idx UNIQUE (channel_id, user_id, question_id)
);
CREATE INDEX idx_channel_user ON ch_question_replies (channel_id, user_id, question_id);

CREATE TABLE application_version (
    version VARCHAR(32) UNIQUE
);
INSERT INTO application_version(version) VALUES('20240330020421');