#!/usr/bin/env python3
"""
PPD Bulk Loader — loads all HMLR Price Paid Data CSVs into staging.ppd_raw
via direct psycopg2 COPY (no Docker intermediary, handles quoting correctly).

Usage:
    pip install psycopg2-binary
    python load_ppd.py
"""

import os
import glob
import time
import psycopg2

# ── Config ────────────────────────────────────────────────────────────────────
PPD_DIR   = r"C:\ccview\site_selector\data\features\ppd"
DB_HOST   = "localhost"
DB_PORT   = 5432
DB_NAME   = "proposiq"
DB_USER   = "proposiq"
DB_PASS   = "proposiq_dev_pw"
# ─────────────────────────────────────────────────────────────────────────────

COPY_SQL = """
COPY staging.ppd_raw (
    transaction_id, price, transfer_date, postcode,
    property_type, new_build, tenure,
    paon, saon, street, locality, town, district, county,
    ppd_category, record_status
)
FROM STDIN
WITH (
    FORMAT csv,
    QUOTE '"',
    ESCAPE '\\',
    NULL ''
)
"""

def load_file(conn, filepath: str) -> int:
    filename = os.path.basename(filepath)
    with conn.cursor() as cur:
        with open(filepath, "r", encoding="utf-8", errors="replace") as f:
            cur.copy_expert(COPY_SQL, f)
        conn.commit()
        return cur.rowcount

def main():
    files = sorted(glob.glob(os.path.join(PPD_DIR, "*.csv")))
    if not files:
        print(f"No CSV files found in {PPD_DIR}")
        return

    print(f"Found {len(files)} files to load\n")

    conn = psycopg2.connect(
        host=DB_HOST, port=DB_PORT,
        dbname=DB_NAME, user=DB_USER, password=DB_PASS
    )

    total_rows = 0
    total_start = time.time()

    for i, filepath in enumerate(files, 1):
        filename = os.path.basename(filepath)
        start = time.time()
        try:
            rows = load_file(conn, filepath)
            elapsed = time.time() - start
            total_rows += rows
            print(f"[{i:>2}/{len(files)}] {filename:<30} {rows:>8,} rows  {elapsed:.1f}s")
        except Exception as e:
            conn.rollback()
            print(f"[{i:>2}/{len(files)}] {filename:<30} ERROR: {e}")

    conn.close()

    total_elapsed = time.time() - total_start
    print(f"\nDone. {total_rows:,} rows loaded in {total_elapsed:.0f}s")

    # Quick sanity check
    conn2 = psycopg2.connect(
        host=DB_HOST, port=DB_PORT,
        dbname=DB_NAME, user=DB_USER, password=DB_PASS
    )
    with conn2.cursor() as cur:
        cur.execute("SELECT COUNT(*) FROM staging.ppd_raw")
        count = cur.fetchone()[0]
        cur.execute("SELECT MIN(transfer_date), MAX(transfer_date) FROM staging.ppd_raw")
        min_date, max_date = cur.fetchone()
    conn2.close()

    print(f"\nTable staging.ppd_raw:")
    print(f"  Total rows : {count:,}")
    print(f"  Date range : {min_date} → {max_date}")

if __name__ == "__main__":
    main()