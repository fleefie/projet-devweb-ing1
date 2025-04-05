CREATE TABLE device_reports (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    reporter_id BIGINT NOT NULL,
    device_id BIGINT NOT NULL,
    reason TEXT NOT NULL,
    report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE,
    
    UNIQUE(reporter_id, device_id)
);

CREATE INDEX idx_device_reports_reporter ON device_reports(reporter_id);
CREATE INDEX idx_device_reports_device ON device_reports(device_id);

