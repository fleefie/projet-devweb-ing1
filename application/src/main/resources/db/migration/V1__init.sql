CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'ROLE_USER' -- Provide default to avoid errors
);

INSERT INTO users (username, password, role)
VALUES (
    'admin', 
    '$2a$10$Wz2hdHcP0ZOwKD5LfWl1KeJqMIhMge/mVxg.EPbnO8Pz3yy7R3m8W', -- BCrypt hash of 'admin123'
    'ROLE_ADMIN'
)
ON CONFLICT(username) DO NOTHING;
