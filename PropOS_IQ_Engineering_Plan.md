# PropOS IQ -- National Spatial Intelligence Platform

## Engineering Execution Plan (Disruptor Roadmap)

------------------------------------------------------------------------

# PHASE 1 -- Spatial Core Infrastructure

## 1.1 Spatial Database Foundation

-   Install PostgreSQL
-   Install PostGIS extension
-   Configure spatial indexing (GiST)
-   Establish schema strategy:
    -   raw_data
    -   spatial_boundaries
    -   features
    -   analytics
    -   api_views

## 1.2 Boundary Data Ingestion

-   Import ONS LSOA boundaries
-   Import MSOA boundaries
-   Import Ward boundaries
-   Import Local Authority boundaries
-   Implement boundary versioning system
-   Build spatial lookup table (postcode → LSOA)

## 1.3 Transaction Spatial Join

-   Geocode PPD transactions to coordinates
-   Assign each transaction to LSOA via spatial join
-   Store LSOA transaction aggregates
-   Validate spatial consistency

------------------------------------------------------------------------

# PHASE 2 -- Feature Engineering Layer

## 2.1 Census Integration

-   Import population datasets
-   Age band distribution
-   Household composition
-   Ethnic diversity metrics
-   Income / occupation indicators
-   Build normalized feature tables per LSOA

## 2.2 Deprivation Index (IMD)

-   Import IMD datasets
-   Normalize deprivation scores
-   Map IMD deciles to LSOA

## 2.3 Education Layer

-   Import Ofsted ratings
-   Link schools to LSOA via spatial join
-   Compute education score per LSOA

## 2.4 Infrastructure Layer

-   Import transport nodes (stations, major roads)
-   Import broadband/fibre data
-   Calculate infrastructure density metrics

------------------------------------------------------------------------

# PHASE 3 -- Analytics & Scoring Engine

## 3.1 Statistical Core Refactor

-   Replace outward postcode with GeoArea abstraction
-   Create GeoArea interface:
    -   LsoaArea
    -   MsoaArea
    -   PolygonArea
    -   RadiusArea
-   Refactor PriceDistributionEngine to operate on geometry

## 3.2 Market Scoring

-   Implement Stability Score (based on CV)
-   Implement Value Score (relative median normalization)
-   Implement Liquidity Score (transaction velocity)
-   Implement Confidence Weighting
-   Composite MarketScore (0--100)

## 3.3 Risk & Classification

-   RiskLevel enum
-   MarketBucket enum
-   MarketSummary generation logic

------------------------------------------------------------------------

# PHASE 4 -- Predictive Layer

## 4.1 ML Feature Store

-   Build feature matrix per LSOA
-   Prepare training datasets
-   Normalize features

## 4.2 Random Forest Regression (SMILE)

-   Price forecasting model
-   Growth probability model
-   Validation & backtesting framework

## 4.3 Scenario Modelling

-   Infrastructure uplift simulation
-   Planning approval impact model
-   Demographic shift sensitivity analysis

------------------------------------------------------------------------

# PHASE 5 -- Generative AI Reasoning Layer

## 5.1 Structured Query Interface

-   Natural language → structured query translator
-   Geo reasoning integration

## 5.2 Explainability Engine

-   Generate reasoning summaries
-   Trade-off analysis explanation
-   Confidence-weighted interpretation

## 5.3 Municipal Intelligence Mode

-   Housing pressure index
-   Regeneration opportunity index
-   Infrastructure adequacy score
-   Policy impact modelling

------------------------------------------------------------------------

# Governance & Scaling

-   Dataset versioning
-   Boundary update management
-   Feature lineage tracking
-   Audit logging
-   Performance indexing strategy
-   API rate limiting

------------------------------------------------------------------------

# Strategic Objective

Build the UK's first explainable, AI-ready, multi-layer spatial
intelligence engine for property, policy, and capital allocation
decisions.
