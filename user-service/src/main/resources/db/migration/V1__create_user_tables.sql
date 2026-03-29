CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE user_profiles (
    id UUID PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    avatar_url VARCHAR(500),
    bio VARCHAR(500),
    headline VARCHAR(200),
    website VARCHAR(200),
    linkedin VARCHAR(200),
    twitter VARCHAR(200),
    role VARCHAR(20) NOT NULL,
    is_instructor_verified BOOLEAN DEFAULT FALSE,
    total_students INTEGER DEFAULT 0,
    total_courses INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_profiles_email ON user_profiles(email);
