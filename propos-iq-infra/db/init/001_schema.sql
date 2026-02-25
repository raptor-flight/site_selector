CREATE EXTENSION IF NOT EXISTS postgis;

CREATE SCHEMA IF NOT EXISTS audit;
CREATE SCHEMA IF NOT EXISTS ref;
CREATE SCHEMA IF NOT EXISTS core;
CREATE SCHEMA IF NOT EXISTS feat;
CREATE SCHEMA IF NOT EXISTS ana;

CREATE TABLE IF NOT EXISTS audit.dataset (
 dataset_id UUID PRIMARY KEY,
 name TEXT NOT NULL,
 source TEXT,
 license TEXT,
 published_date DATE,
 ingested_at TIMESTAMPTZ NOT NULL DEFAULT now(),
 checksum TEXT,
 notes TEXT
);

CREATE TABLE IF NOT EXISTS ref.geo_boundary (
boundary_id UUID PRIMARY KEY,
geo_type TEXT NOT NULL,                -- LSOA/MSOA/WARD/LA
external_code TEXT NOT NULL,           -- e.g. E01000001
name TEXT,
boundary_version TEXT NOT NULL,        -- e.g. 2021
geom geometry(MultiPolygon, 27700) NOT NULL,
dataset_id UUID REFERENCES audit.dataset(dataset_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_geo_boundary ON ref.geo_boundary(geo_type, external_code, boundary_version);

CREATE INDEX IF NOT EXISTS ix_geo_boundary_geom ON ref.geo_boundary USING GIST (geom);

CREATE TABLE IF NOT EXISTS core.geo_area (
 geo_area_id UUID PRIMARY KEY,
 geo_type TEXT NOT NULL,
 external_code TEXT,
 label TEXT,
 boundary_version TEXT,
 geom geometry(MultiPolygon, 27700) NOT NULL,
 source_boundary_id UUID REFERENCES ref.geo_boundary(boundary_id)
);

CREATE INDEX IF NOT EXISTS ix_geo_area_geom ON core.geo_area USING GIST (geom);

CREATE UNIQUE INDEX IF NOT EXISTS ux_geo_area_natkey ON core.geo_area(geo_type, external_code, boundary_version);