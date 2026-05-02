CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE course_analytics (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    course_id UUID NOT NULL UNIQUE,
    total_enrollments INTEGER DEFAULT 0,
    active_students INTEGER DEFAULT 0,
    completion_rate DOUBLE PRECISION DEFAULT 0.0,
    total_revenue NUMERIC(12,2) DEFAULT 0.00,
    average_rating DOUBLE PRECISION DEFAULT 0.0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_course_analytics_course_id ON course_analytics(course_id);
