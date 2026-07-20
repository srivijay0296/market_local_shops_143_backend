-- Database Optimization Indexing Script

-- 1. Index on Users
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- 2. Indexes on Shops
CREATE INDEX IF NOT EXISTS idx_shops_market_status ON shops(market_id, status);
CREATE INDEX IF NOT EXISTS idx_shops_owner ON shops(owner_id);

-- 3. Indexes on Products
CREATE INDEX IF NOT EXISTS idx_products_shop_category ON products(shop_id, category);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);

-- 4. Indexes on Orders and Order Items
CREATE INDEX IF NOT EXISTS idx_orders_user_status ON orders(user_id, status);
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product ON order_items(product_id);

-- 5. Indexes on Social Feed & Reels (Seller Posts)
CREATE INDEX IF NOT EXISTS idx_seller_posts_shop_media ON seller_posts(shop_id, media_type);
CREATE INDEX IF NOT EXISTS idx_seller_posts_status ON seller_posts(status);
CREATE INDEX IF NOT EXISTS idx_post_likes_post_user ON post_likes(post_id, user_id);
CREATE INDEX IF NOT EXISTS idx_post_saves_post_user ON post_saves(post_id, user_id);
CREATE INDEX IF NOT EXISTS idx_post_comments_post ON post_comments(post_id);
