
# PropOS IQ – Core Architecture Overview

This document summarises the internal architecture of PropOS IQ and clarifies the responsibility boundaries between Controllers, Assemblers, Engines, and Data Providers.

This is the **non‑LLM system core**. NLP and GenAI layers wrap this — they do not replace it.

---

# Architectural Principles

PropOS IQ follows a strict layered architecture:

- Controllers are entry points only
- Assemblers orchestrate domain logic
- Engines perform analytics and ML/statistics
- DataProviders/DAOs fetch raw data
- Dependencies only flow downward
- No internal REST calls between layers

**Rule:** Controllers call Assemblers → Assemblers call Engines → Engines call DataProviders

Never the other way around.

---

# Layer Responsibilities

## Controllers (REST Gateways)

Examples:
- APIPriceGateway
- APICompareGateway
- NLPGateway

Responsibilities:
- Accept HTTP input
- Perform light validation
- Parse query params (or NLP text in NLPGateway)
- Call Assemblers
- Return View models

Controllers contain:
- No analytics logic
- No ML/statistics
- No CSV/DB access

---

## Assemblers (Domain Orchestrators)

Examples:
- PriceDistributionViewAssembler
- PriceCompareAssembler

Responsibilities:
- Normalize inputs (postcode parsing, defaults)
- Call one or more Engines
- Combine results
- Map Engine models → API View models

Assemblers are:
- Deterministic
- Reusable
- Called by both System APIs and NLP APIs

---

## Engines (Analytics Core)

Examples:
- PPDAnalyticsEngine
- Future: GrowthEngine, RiskEngine, MLValuationEngine

Responsibilities:
- Perform statistical and ML calculations
- Use SMILE / MetricAlgo
- Work on numeric arrays and features
- Remain independent of REST and NLP

Engines contain:
- No HTTP
- No JSON
- No NLP parsing
- No controller dependencies

---

## Data Providers / DAO

Examples:
- CSVPpdDataProvider
- Future: DatabasePpdDataProvider

Responsibilities:
- Fetch raw datasets
- Filter basic criteria
- Return domain records

No analytics belongs here.

---

# Two API Styles in PropOS IQ

## 1️⃣ System APIs (Structured, Deterministic)

Audience:
- Dashboards
- Integrations
- Partners
- LLM tool-calling
- Internal services

Examples:

    /price/distribution
    /price/compare

Input:
- Structured parameters

Output:
- Deterministic analytics Views

Flow:

    Client / Dashboard
            ↓
    System API Controller
            ↓
        Assembler
            ↓
         Engine
            ↓
     DataProvider

---

## 2️⃣ NLP API (Free Text Interface)

Audience:
- Humans
- Chat UI
- GenAI agents

Examples:

    /nlp/query?q=compare median prices in B38 and B15 over 5 years

Flow:

    Human / Chat UI
            ↓
        NLPGateway
            ↓
       QueryParser
            ↓
        Assembler   (same assembler as System API)
            ↓
         Engine
            ↓
     DataProvider

Important:
- NLP layer translates language → structured parameters
- It does not duplicate analytics logic

---

# Critical Rule: No Internal REST Calls

Engines and Assemblers must never call REST controllers internally.

❌ Wrong:

    Engine → HTTP → System API → Engine

✅ Correct:

    Controller → Assembler → Engine

---

# Why This Matters

This structure ensures:

- Deterministic analytics
- Testable engines
- Reusable domain logic
- Safe GenAI integration
- Enterprise‑grade API contracts
- No hallucinated results from LLMs

System APIs become the **ground truth analytics layer** that GenAI wraps safely.

---

# Mental Model

System APIs = Brain  
NLP API = Ears & Mouth  
Engines = Math & ML Core  
Assemblers = Coordinators  

---

# Next Extension Layers (Future)

- Neighbourhood Analytics Engine
- Lender Risk Engine
- ML Valuation Engine (Random Forest)
- Census + Diversity Engine
- LangChain4j Agent Layer

All will plug into this same architecture.
