CREATE TABLE feat.area_deprivation (
    id                      SERIAL PRIMARY KEY,
    geo_area_id             UUID NOT NULL REFERENCES core.geo_area(geo_area_id),
    lsoa_code               VARCHAR(9) NOT NULL,
    lsoa_name               VARCHAR(255),

    -- Overall IMD
    imd_score               NUMERIC(10,4),
    imd_rank                INTEGER,
    imd_decile              SMALLINT,

    -- Domain scores
    income_score            NUMERIC(10,4),
    income_rank             INTEGER,
    employment_score        NUMERIC(10,4),
    employment_rank         INTEGER,
    education_score         NUMERIC(10,4),
    education_rank          INTEGER,
    health_score            NUMERIC(10,4),
    health_rank             INTEGER,
    crime_score             NUMERIC(10,4),
    crime_rank              INTEGER,
    housing_score           NUMERIC(10,4),
    housing_rank            INTEGER,
    environment_score       NUMERIC(10,4),
    environment_rank        INTEGER,

    -- Population
    total_population        INTEGER,
    dependent_children      INTEGER,
    older_population        INTEGER,

    -- Provenance
    dataset_id              INTEGER REFERENCES audit.dataset(id),
    valid_from              DATE DEFAULT '2019-01-01',
    created_at              TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT uq_deprivation_area UNIQUE (geo_area_id)
);

-- Index for fast lookups
CREATE INDEX idx_area_deprivation_geo ON feat.area_deprivation(geo_area_id);
CREATE INDEX idx_area_deprivation_decile ON feat.area_deprivation(imd_decile);
CREATE INDEX idx_area_deprivation_rank ON feat.area_deprivation(imd_rank);