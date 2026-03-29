#!/usr/bin/env python3
"""
CCOD/OCOD Bulk Loader — loads HMLR corporate and overseas ownership data
into staging.ccod_raw and staging.ocod_raw.
Handles HMLR footer row (Row Count:) transparently.

Usage:
    pip install psycopg2-binary
    python load_ownership.py
"""

import os
import time
import psycopg2

# ── Config ────────────────────────────────────────────────────────────────────
CCOD_FILE = r"C:\ccview\site_selector\data\features\ccod\CCOD_FULL_2026_03.csv"
OCOD_FILE = r"C:\ccview\site_selector\data\features\ocod\OCOD_FULL_2026_03.csv"

DB_HOST   = "localhost"
DB_PORT   = 5432
DB_NAME   = "proposiq"
DB_USER   = "proposiq"
DB_PASS   = "proposiq_dev_pw"
# ─────────────────────────────────────────────────────────────────────────────

CCOD_COPY = """
COPY staging.ccod_raw FROM STDIN
WITH (FORMAT csv, HEADER true, QUOTE '"', ESCAPE '\\', NULL '')
"""

OCOD_COPY = """
COPY staging.ocod_raw FROM STDIN
WITH (FORMAT csv, HEADER true, QUOTE '"', ESCAPE '\\', NULL '')
"""


class FooterStrippingReader:
    """
    Wraps a file object and stops streaming at the HMLR footer row.
    HMLR appends a 'Row Count:' summary line at the end of their CSVs
    which breaks PostgreSQL COPY — this strips it transparently.
    """
    def __init__(self, f):
        self.f = f
        self.done = False

    def read(self, size=-1):
        if self.done:
            return ""
        chunk = self.f.read(size if size > 0 else 1024 * 1024)
        if not chunk:
            return ""
        idx = chunk.find('"Row Count:')
        if idx != -1:
            self.done = True
            return chunk[:idx]
        return chunk


def load_file(conn, filepath: str, copy_sql: str, label: str) -> int:
    print(f"Loading {label} from {os.path.basename(filepath)}...")
    start = time.time()
    with conn.cursor() as cur:
        with open(filepath, "r", encoding="utf-8", errors="replace") as f:
            cur.copy_expert(copy_sql, FooterStrippingReader(f))
        conn.commit()
        elapsed = time.time() - start
        print(f"Done: {cur.rowcount:,} rows in {elapsed:.1f}s")
        return cur.rowcount


def main():
    conn = psycopg2.connect(
        host=DB_HOST, port=DB_PORT,
        dbname=DB_NAME, user=DB_USER, password=DB_PASS
    )

    total = 0
    total += load_file(conn, CCOD_FILE, CCOD_COPY, "CCOD")
    total += load_file(conn, OCOD_FILE, OCOD_COPY, "OCOD")

    conn.close()

    # Sanity check
    conn2 = psycopg2.connect(
        host=DB_HOST, port=DB_PORT,
        dbname=DB_NAME, user=DB_USER, password=DB_PASS
    )
    with conn2.cursor() as cur:
        cur.execute("SELECT COUNT(*) FROM staging.ccod_raw")
        ccod_count = cur.fetchone()[0]
        cur.execute("SELECT COUNT(*) FROM staging.ocod_raw")
        ocod_count = cur.fetchone()[0]
        cur.execute("""
            SELECT country_incorporated_1, COUNT(*)
            FROM staging.ocod_raw
            WHERE country_incorporated_1 IS NOT NULL
            GROUP BY country_incorporated_1
            ORDER BY count DESC
            LIMIT 10
        """)
        top_countries = cur.fetchall()
    conn2.close()

    print(f"\nstaging.ccod_raw: {ccod_count:,} rows")
    print(f"staging.ocod_raw: {ocod_count:,} rows")
    print(f"\nTop overseas countries:")
    for country, count in top_countries:
        print(f"  {country}: {count:,}")


if __name__ == "__main__":
    main()