ALTER TABLE users ADD COLUMN birthdate TEXT;
ALTER TABLE users ADD COLUMN gender TEXT;

UPDATE users SET birthdate = '2000-01-01' WHERE birthdate IS NULL;
UPDATE users SET gender = 'unspecified' WHERE gender IS NULL;
