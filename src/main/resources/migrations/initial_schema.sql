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

CREATE TABLE IF NOT EXISTS ch_questions (
    id UUID PRIMARY KEY,
    question_id INT,
    channel_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ch_question_replies (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(32),
    channel_id VARCHAR(100),
    question_id UUID,
    is_correct BOOLEAN,
    CONSTRAINT unique_channel_user_question_idx UNIQUE (channel_id, user_id, question_id)
);
CREATE INDEX IF NOT EXISTS idx_channel_user ON ch_question_replies (channel_id, user_id, question_id)