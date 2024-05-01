CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    channel_id VARCHAR(32),
    task_name VARCHAR(50),
    is_active BOOLEAN DEFAULT true,
    frequency INTEGER,
    last_executed_at TIMESTAMP WITHOUT TIME ZONE,
    next_execution_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT unique_channel_task_idx UNIQUE (channel_id, task_name)
);
CREATE INDEX last_executed_at_idx ON tasks (last_executed_at);

INSERT INTO tasks(channel_id, task_name, frequency, last_executed_at, next_execution_at)
    SELECT id, 'share_fact', 86400, NOW(), NOW() FROM channels c WHERE c.is_public=true;

INSERT INTO tasks(channel_id, task_name, frequency, last_executed_at, next_execution_at)
    SELECT id, 'ask_question', 10800, NOW(), NOW() FROM channels c WHERE c.is_public=true;
