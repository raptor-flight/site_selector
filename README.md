
# 🏠 Property AI Analytics Platform

## Overview (Plain English)
This application lets users ask **natural language questions** about UK property prices, such as:

> “What is the average house price in B38 over the last 5 years?”  
> “Compare average prices between B17 and B38 since 2010.”

Instead of manually querying spreadsheets, databases, or dashboards, users interact with the system conversationally.  
Behind the scenes, the platform parses the question, applies deterministic analytics, and returns **explainable, auditable results**.

This is **not a chatbot guessing answers** — it is a **data-driven analytics engine enhanced with NLP and AI**.

---

## What Problem Does This Solve?
- Property data is large, fragmented, and hard to query
- Non-technical users cannot easily ask analytical questions
- Generic LLMs do not have direct access to structured datasets
- Answers need to be **accurate, reproducible, and explainable**

This platform bridges that gap.

---

## Core Features
- Natural language query parsing (NLP)
- Intent & metric detection (Average, Compare, Trends)
- Postcode normalization (full & outward codes)
- Time-based filtering (last N years, date ranges)
- Deterministic analytics using Java Streams & SMILE
- REST API for frontend or integration use
- Metadata-rich responses (sample size, ranges, assumptions)

---

## Example Query
```
GET /site-selector/nlp/query?q=compare average price in B17 8PU and B38 over the last 3 years
```

### Example Response
```json
{
  "interpretation": "compare average price in B17 8PU and B38 over the last 3 years",
  "metaData": [
    {
      "postCode": "B17 8PU",
      "fromYear": 2022,
      "toYear": 2025,
      "sampleSize": 2,
      "value": 662500.0
    },
    {
      "postCode": "B38",
      "fromYear": 2022,
      "toYear": 2025,
      "sampleSize": 4,
      "value": 224500.0
    }
  ]
}
```

---

## High-Level Architecture

```
User Query
   ↓
REST API (Quarkus / JAX-RS)
   ↓
NLP Parser (Regex + Intent Engine)
   ↓
Business Service Layer
   ↓
Analytics Engine (Streams + SMILE)
   ↓
Structured JSON Response
```

---

## Technical Architecture (Detailed)

### 1. REST Layer
- Built with **Quarkus + RESTEasy Reactive**
- Stateless endpoints
- Supports JSON-first API design

### 2. NLP Layer
- Regex-driven parsing (postcodes, time ranges, metrics)
- Intent classification:
    - AVERAGE_PRICE
    - COMPARE_AREAS
    - FUTURE: TREND_ANALYSIS, OUTLIERS, FORECASTING

### 3. Domain Model
- `PropertyQuery`
- `PropertyIntentQueryType`
- `IntentMetric`
- Immutable result objects

### 4. Analytics Layer
- Java Streams for deterministic aggregation
- SMILE DataFrames for future ML/statistical models
- Explicit normalization and filtering

### 5. Data Source
- UK Land Registry PPD CSV (currently)
- Designed to support:
    - Relational DB
    - Cloud storage
    - Data lakes

---

## Why Not Just Use ChatGPT?

| ChatGPT | This Platform |
|------|-------------|
| No live data access | Direct dataset access |
| Non-deterministic answers | Deterministic analytics |
| No audit trail | Full transparency |
| No domain guarantees | Property-specific logic |
| General-purpose | Vertical-specialized |

**LLMs enhance this system — they do not replace it.**

---

## AI Roadmap

### Phase 1 (Current)
- Rule-based NLP
- Deterministic metrics
- Transparent results

### Phase 2
- ML-based intent classification
- Statistical confidence intervals
- Trend detection

### Phase 3
- Price forecasting models
- Anomaly detection
- Area similarity clustering

### Phase 4
- LLM-powered conversational analyst
- Query suggestions
- Insight summarization

---

## Business Model Vision
- Freemium tier (basic analytics)
- Professional tier (comparisons, trends)
- Enterprise tier (API access, bulk queries)
- Geographic expansion beyond UK

---

## Why This Project Matters
- Combines **engineering discipline** with **AI**
- Solves a real-world data accessibility problem
- Designed for scale, accuracy, and trust
- Not hype-driven — outcome-driven

---

## Status
🚧 Actively developed  
🔍 NLP-first, ML-ready  
🌍 Built for global expansion

---

## Author Vision
This platform aims to become a **global property intelligence layer**, enabling governments, businesses, and individuals to ask intelligent questions about real-world assets — accurately and transparently.
