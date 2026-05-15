INSERT INTO users (username, password_hash, score) VALUES
  ('alice', 'hash1', 1500),
  ('bob', 'hash2', 1400),
  ('carol', 'hash3', 1300);

INSERT INTO games (state, white_id, black_id) VALUES
  ('ready',   1, 2),
  ('started', 2, 3),
  ('finished',3, 1);
