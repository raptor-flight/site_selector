# site_selector
<h2>AI powered site selection and redevelopment finder</h2>
<p><em>Useful links</em></p>
<br>
<a href="https://landregistry.data.gov.uk/app/ppd/">HMLR.</a>
<a href="https://www.gov.uk/guidance/about-the-price-paid-data">PPD Column Data dictionary</a>
<a href="https://developer.ons.gov.uk/">Office of National Statistics - ONS.</a>
<a href="https://explore-education-statistics.service.gov.uk/data-catalogue/data-set/8dc33d4f-62b7-4244-9bc9-dc73ce7f05f3/">IDACI (Income Deprivation Affecting Children Index) decile and degree of rurality of pupil residence data.</a>


                 ┌────────────────────┐
                 │ HM Land Registry   │
                 │   (PPD CSV)        │
                 └─────────┬──────────┘
                           │
                           ▼
                 ┌────────────────────┐
                 │  Java Parser       │
                 │ (POJOs: PropertyRecord) │
                 └─────────┬──────────┘
                           │
                           ▼
                 ┌────────────────────┐
                 │  Smile DataFrame   │
                 │  (Structured data) │
                 └─────────┬──────────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
┌──────────────┐   ┌──────────────┐   ┌─────────────────┐
│ Descriptive  │   │ Predictive   │   │ Data Cleaning   │
│ Analytics    │   │ Models       │   │ (outliers etc.) │
│ (mean, trends│   │ (regression, │   └─────────────────┘
│  groupBy)    │   │ clustering)  │
└─────┬────────┘   └─────┬────────┘
│                  │
▼                  ▼
┌────────────────────────────────────┐
│ Results:                           │
│  - Price trends per postcode       │
│  - Forecasts for property types    │
│  - Clean dataset                   │
└───────────────┬────────────────────┘
│
▼
┌─────────────────────────┐
│   Large Language Model  │
│ (ChatGPT / LLaMA via API│
│ or fine-tuned model)    │
└───────────┬─────────────┘
│
▼
┌─────────────────────────┐
│ Natural Language Output │
│ - Investor reports      │
│ - Q&A interface         │
│ - Summaries of planning │
│   applications          │
└─────────────────────────┘





