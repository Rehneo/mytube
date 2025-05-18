CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
CREATE TYPE video_status AS ENUM ('ACTIVE', 'DELETED');
CREATE TYPE video_report_status AS ENUM ('PENDING', 'REJECTED', 'APPROVED');
CREATE TYPE ban_type AS ENUM ('STRIKES', 'OTHER');
CREATE TYPE moderation_status as ENUM('PENDING','REVIEW', 'PASSED','REJECTED');

CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(128) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    role     user_role    NOT NULL DEFAULT 'USER'
);

CREATE TABLE subscriptions
(
    id            SERIAL PRIMARY KEY,
    subscriber_id INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    channel_id    INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE videos
(
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(255)             NOT NULL,
    channel_id      INTEGER                  NOT NULL REFERENCES users (id),
    description     TEXT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    status          video_status             NOT NULL DEFAULT 'ACTIVE',
    moderation_status moderation_status NOT NULL DEFAULT 'PENDING',
    deleted_at      TIMESTAMP WITH TIME ZONE,
    deleted_by      user_role,
    deletion_reason TEXT
);

CREATE TABLE strikes
(
    id       SERIAL PRIMARY KEY,
    user_id  INTEGER NOT NULL REFERENCES users (id),
    reason   TEXT,
    video_id INTEGER REFERENCES videos (id)
);

CREATE TABLE video_reports
(
    id           SERIAL PRIMARY KEY,
    description  TEXT                NOT NULL,
    status       video_report_status NOT NULL DEFAULT 'PENDING',
    video_id     INTEGER             NOT NULL REFERENCES videos (id),
    sender_id    INTEGER             NOT NULL REFERENCES users (id),
    created_at   TIMESTAMP WITH TIME ZONE     DEFAULT now(),
    processed_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE bans
(
    id      SERIAL PRIMARY KEY,
    type    ban_type NOT NULL DEFAULT 'STRIKES',
    reason  TEXT,
    user_id INTEGER  NOT NULL REFERENCES users (id)
);

CREATE TABLE forbidden_words
(
    id SERIAL PRIMARY KEY,
    word VARCHAR(64) NOT NULL UNIQUE
);