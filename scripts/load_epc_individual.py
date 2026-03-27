import psycopg2
import csv
import os

conn = psycopg2.connect(
    host="localhost", port=5432,
    database="proposiq", user="proposiq", password="proposiq_dev_pw"
)
cur = conn.cursor()

# Create staging table
cur.execute("""
    CREATE TABLE IF NOT EXISTS ingest.epc_raw (
        postcode                VARCHAR(8),
        current_energy_rating   CHAR(1),
        potential_energy_rating CHAR(1),
        current_efficiency      SMALLINT,
        potential_efficiency    SMALLINT,
        property_type           VARCHAR(30),
        construction_age_band   VARCHAR(50),
        tenure                  VARCHAR(30),
        inspection_date         DATE,
        total_floor_area        NUMERIC(8,2),
        co2_emissions_current   NUMERIC(8,2),
        mains_gas_flag          CHAR(1)
    );
    CREATE INDEX IF NOT EXISTS idx_epc_raw_postcode 
        ON ingest.epc_raw(postcode);
    CREATE INDEX IF NOT EXISTS idx_epc_raw_rating 
        ON ingest.epc_raw(current_energy_rating);
""")
conn.commit()
print('Staging table ready')

base = r'C:\ccview\site_selector\data\features\energy'
total_props = 0
total_folders = 0

VALID_RATINGS = {'A','B','C','D','E','F','G'}

def clean_date(val):
    val = val.strip()
    if not val:
        return None
    try:
        from datetime import datetime
        return datetime.strptime(val[:10], '%Y-%m-%d').date()
    except:
        return None

def clean_num(val):
    val = val.strip()
    try:
        return float(val) if val else None
    except:
        return None

def clean_int(val):
    val = val.strip()
    try:
        v = float(val)
        return int(v) if 0 <= v <= 32767 else None
    except:
        return None

sql = """
    INSERT INTO ingest.epc_raw (
        postcode, current_energy_rating, potential_energy_rating,
        current_efficiency, potential_efficiency,
        property_type, construction_age_band, tenure,
        inspection_date, total_floor_area,
        co2_emissions_current, mains_gas_flag
    ) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
"""

for folder in sorted(os.listdir(base)):
    cert_path = os.path.join(base, folder, 'certificates.csv')
    if not os.path.exists(cert_path):
        continue

    batch = []
    count = 0

    with open(cert_path, encoding='utf-8', errors='replace') as f:
        reader = csv.reader(f)
        next(reader)  # skip header
        for row in reader:
            if len(row) < 88:
                continue
            postcode = row[4].strip().replace(' ', '').upper()
            if not postcode:
                continue
            rating = row[6].strip().upper()
            if rating not in VALID_RATINGS:
                continue

            batch.append((
                postcode,
                rating,
                row[7].strip().upper() or None,
                clean_int(row[8]),
                clean_int(row[9]),
                row[10].strip()[:30] or None,
                row[85].strip()[:50] or None,
                row[87].strip()[:30] or None,
                clean_date(row[12]),
                clean_num(row[31]),
                clean_num(row[22]),
                row[33].strip()[:1] or None,
            ))

            if len(batch) >= 50000:
                try:
                    cur.executemany(sql, batch)
                    conn.commit()
                    count += len(batch)
                    total_props += len(batch)
                except Exception as e:
                    print(f'  BATCH ERROR: {e}')
                    conn.rollback()
                batch = []

    if batch:
        try:
            cur.executemany(sql, batch)
            conn.commit()
            count += len(batch)
            total_props += len(batch)
        except Exception as e:
            print(f'  BATCH ERROR: {e}')
            conn.rollback()

    total_folders += 1
    print(f'  [{total_folders}/347] {folder}: {count:,} records')

cur.close()
conn.close()
print(f'\nDone: {total_folders} folders, {total_props:,} total EPC records')