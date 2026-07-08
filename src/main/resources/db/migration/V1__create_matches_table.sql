CREATE TABLE matches (
    id BIGSERIAL PRIMARY KEY,
    match_date DATE NOT NULL,
    match_time TIME,
    referee_role VARCHAR(50) NOT NULL,
    age_group VARCHAR(100),
    division VARCHAR(100),
    home_team VARCHAR(150) NOT NULL,
    away_team VARCHAR(150) NOT NULL,
    venue VARCHAR(150),
    status VARCHAR(50) NOT NULL,
    home_goals INTEGER,
    away_goals INTEGER
);