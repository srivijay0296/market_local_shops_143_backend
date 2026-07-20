CREATE TABLE IF NOT EXISTS seller_posts (
    id SERIAL PRIMARY KEY,
    shop_id INTEGER REFERENCES shops(id) ON DELETE CASCADE,
    title VARCHAR(255),
    description TEXT,
    media_url TEXT,
    media_type VARCHAR(50),
    video_url TEXT,
    price DECIMAL(10, 2),
    offer_tag VARCHAR(100),
    location VARCHAR(255),
    category VARCHAR(255),
    status VARCHAR(50) DEFAULT 'approved',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS seller_post_media_urls (
    seller_post_id INTEGER REFERENCES seller_posts(id) ON DELETE CASCADE,
    media_url TEXT,
    PRIMARY KEY (seller_post_id, media_url)
);

CREATE TABLE IF NOT EXISTS post_likes (
    id SERIAL PRIMARY KEY,
    post_id INTEGER REFERENCES seller_posts(id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_post_user_like UNIQUE (post_id, user_id)
);

CREATE TABLE IF NOT EXISTS post_saves (
    id SERIAL PRIMARY KEY,
    post_id INTEGER REFERENCES seller_posts(id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_post_user_save UNIQUE (post_id, user_id)
);

CREATE TABLE IF NOT EXISTS post_comments (
    id SERIAL PRIMARY KEY,
    post_id INTEGER REFERENCES seller_posts(id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
