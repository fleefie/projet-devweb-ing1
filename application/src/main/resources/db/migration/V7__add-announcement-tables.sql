-- Don't ask why this is here. I wish I had no answer for you,
-- but I do. And the fact that I know why this is here makes me SAD.
DROP TABLE IF EXISTS announcement_reports;
DROP TABLE IF EXISTS announcement_role_restrictions;
DROP TABLE IF EXISTS announcements;

CREATE TABLE announcements (
    id INTEGER PRIMARY KEY,
    title TEXT NOT NULL,
    body TEXT NOT NULL,
    poster_id INTEGER NOT NULL,
    tags TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    is_public INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (poster_id) REFERENCES users(id)
);

CREATE TABLE announcement_role_restrictions (
    announcement_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (announcement_id, role_id),
    FOREIGN KEY (announcement_id) REFERENCES announcements(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE announcement_reports (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    reporter_id BIGINT NOT NULL,
    announcement_id BIGINT NOT NULL,
    reason TEXT NOT NULL,
    report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (announcement_id) REFERENCES announcement(id) ON DELETE CASCADE,
    
    UNIQUE(reporter_id, announcement_id)
);

CREATE INDEX idx_announcements_poster ON announcements(poster_id);
CREATE INDEX idx_announcements_title ON announcements(title);
CREATE INDEX idx_reports_announcement ON announcement_reports(announcement_id);

-- FUCK POSTGRESQL
