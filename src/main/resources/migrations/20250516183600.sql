ALTER SEQUENCE learned_words_id_seq RENAME TO learned_resources_seq;

UPDATE learned_resources SET resource_type = UPPER(resource_type);