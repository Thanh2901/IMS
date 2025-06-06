CREATE SEQUENCE IF NOT EXISTS history_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS infra_image_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE if not exists event
(
    id              VARCHAR(36)     NOT NULL,
    confidence      DOUBLE PRECISION NOT NULL,
    date_captured   TIMESTAMP WITHOUT TIME ZONE,
    end_time        TIMESTAMP WITHOUT TIME ZONE,
    event_status    VARCHAR(15),
    level           INTEGER,
    status          VARCHAR(15),
    image_id        BIGINT,
    infra_object_id VARCHAR(36),
    CONSTRAINT event_pkey PRIMARY KEY (id)
);

CREATE TABLE if not exists fake_event
(
    id         VARCHAR(36)     NOT NULL,
    category   VARCHAR(25),
    confidence DOUBLE PRECISION NOT NULL,
    latitude   DOUBLE PRECISION NOT NULL,
    location   VARCHAR(255),
    longitude  DOUBLE PRECISION NOT NULL,
    name       VARCHAR(50),
    status     VARCHAR(50),
    time       TIMESTAMP WITHOUT TIME ZONE,
    image_id   BIGINT,
    CONSTRAINT fake_event_pkey PRIMARY KEY (id)
);

CREATE TABLE if not exists history
(
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    confidence      DOUBLE PRECISION                        NOT NULL,
    date_captured   TIMESTAMP WITHOUT TIME ZONE,
    level           INTEGER,
    status          VARCHAR(50),
    image_id        BIGINT,
    infra_object_id VARCHAR(36),
    CONSTRAINT history_pkey PRIMARY KEY (id)
);

CREATE TABLE if not exists infra_image
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    path_url VARCHAR(255),
    CONSTRAINT infra_image_pkey PRIMARY KEY (id)
);

CREATE TABLE if not exists infra_objects
(
    id            VARCHAR(36)     NOT NULL,
    additional    VARCHAR(255),
    camera_id     VARCHAR(36),
    category      VARCHAR(50),
    confidence    DOUBLE PRECISION NOT NULL,
    date_captured TIMESTAMP WITHOUT TIME ZONE,
    latitude      DOUBLE PRECISION,
    level         INTEGER,
    location      VARCHAR(255),
    longitude     DOUBLE PRECISION,
    name          VARCHAR(50),
    status        VARCHAR(50),
    image_id      BIGINT,
    CONSTRAINT infra_objects_pkey PRIMARY KEY (id)
);

ALTER TABLE history
    ADD CONSTRAINT fk21npyh6gyd6tg5ai7crrw5swh FOREIGN KEY (infra_object_id) REFERENCES infra_objects (id) ON DELETE NO ACTION;

ALTER TABLE event
    ADD CONSTRAINT fkatftofxuy5phi3gjr5lt9u2aq FOREIGN KEY (infra_object_id) REFERENCES infra_objects (id) ON DELETE NO ACTION;

ALTER TABLE history
    ADD CONSTRAINT fkbtjl1kfgulbpi8raif8okjegx FOREIGN KEY (image_id) REFERENCES infra_image (id) ON DELETE NO ACTION;

ALTER TABLE infra_objects
    ADD CONSTRAINT fkl5csrylihhemlh2tk07g8kglq FOREIGN KEY (image_id) REFERENCES infra_image (id) ON DELETE NO ACTION;

ALTER TABLE fake_event
    ADD CONSTRAINT fkmv85cnctibu7sddy2ansokx26 FOREIGN KEY (image_id) REFERENCES infra_image (id) ON DELETE NO ACTION;

ALTER TABLE event
    ADD CONSTRAINT fkq0oj6156uqyhyulf0x2fj6a3a FOREIGN KEY (image_id) REFERENCES infra_image (id) ON DELETE NO ACTION;

create extension if not exists unaccent;
CREATE INDEX infra_location_gist ON infra_objects USING GIST (ST_SetSRID(ST_MakePoint(longitude, latitude), 4326));