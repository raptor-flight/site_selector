package com.propos.iq.domain.model.features;

public record PpdHistory( // Identity — null when aggregated at district level
      String  lsoa_code,

      // District aggregation count — null for single LSOA lookups
      Integer lsoa_count,

      // All-time stats (1995–present)
      Long    transaction_count,
      Long    median_price,
      Long    avg_price,
      Long    min_price,
      Long    max_price,

      // Property type breakdown (all-time counts)
      Long    detached_count,
      Long    semi_count,
      Long    terraced_count,
      Long    flat_count,
      Long    new_build_count,

      // Time-windowed medians
      Long    median_price_5yr,   // 2020–present
      Long    median_price_1yr,   // 2024–present

      // Liquidity
      Long    transactions_1yr,

      // Growth signal
      Long    price_growth_5yr_pct
) {}
