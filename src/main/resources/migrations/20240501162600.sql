ALTER TABLE learned_words ADD COLUMN resource_type VARCHAR(10) NOT NULL DEFAULT 'word';
ALTER TABLE learned_words RENAME COLUMN word_id TO resource_id;
ALTER TABLE learned_words RENAME COLUMN channel_id TO group_id;
ALTER TABLE learned_words RENAME TO learned_resources;

CREATE INDEX channel_resource_idx ON learned_resources (group_id, resource_type);

DROP INDEX idx_channel_id;