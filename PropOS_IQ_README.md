# PropOS IQ

## National Spatial Intelligence Engine for the UK

PropOS IQ is an enterprise-grade spatial intelligence platform designed
to transform multi-source UK geographic data into structured,
explainable, and predictive decision analytics.

------------------------------------------------------------------------

## Vision

PropOS IQ aims to become the analytical infrastructure layer for:

-   Local Authorities
-   Property Investors
-   Developers
-   Lenders
-   Urban Planners
-   Policy Makers

This platform integrates transactional, demographic, socio-economic,
infrastructure, and planning datasets into a unified geospatial
reasoning engine.

------------------------------------------------------------------------

## Core Capabilities

### Statistical Market Engine

-   Price distributions (mean, median, percentiles)
-   Risk metrics (CV, IQR, IQR/Median)
-   Liquidity scoring
-   Confidence classification

### Spatial Intelligence Layer

-   LSOA / MSOA boundary modelling
-   Census integration
-   Deprivation indexing
-   Education performance scoring
-   Infrastructure density modelling
-   Retail and amenity mapping

### Market Scoring

-   Stability Score
-   Value Score
-   Liquidity Score
-   Composite MarketScore (0--100)
-   Market classification & buckets

### Predictive Analytics

-   Random Forest price forecasting
-   Growth probability modelling
-   Scenario simulation

### Generative AI Layer

-   Natural language spatial queries
-   Explainable reasoning summaries
-   Trade-off interpretation
-   Policy and investment guidance

------------------------------------------------------------------------

## Architecture

Controller → Assembler → Engine → DAO\
GeoAdapter → Spatial Engine → Feature Store → Analytics Layer → AI Layer

-   Quarkus (RESTEasy Reactive)
-   Java 21
-   SMILE (ML foundation)
-   PostgreSQL + PostGIS
-   ONS / OGL open datasets

------------------------------------------------------------------------

## Why PropOS IQ Matters

The UK property ecosystem lacks:

-   Unified spatial reasoning tools
-   Explainable analytics
-   Multi-layer data integration
-   AI-assisted decision infrastructure

PropOS IQ addresses this by building a geometry-native intelligence
engine capable of municipal-scale modelling.

------------------------------------------------------------------------

## Long-Term Objective

To become the UK's leading spatial intelligence infrastructure for
housing, regeneration, infrastructure planning, and capital allocation.

PropOS IQ is not a dashboard.

It is the analytical brain behind property and policy decisions.
