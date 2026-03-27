import psycopg2
import csv
import os

conn = psycopg2.connect(
    host="localhost", port=5432,
    database="proposiq", user="proposiq", password="proposiq_dev_pw"
)
cur = conn.cursor()

CRIME_TYPES = {
    'Anti-social behaviour', 'Bicycle theft', 'Burglary',
    'Criminal damage and arson', 'Drugs', 'Other crime', 'Other theft',
    'Possession of weapons', 'Public order', 'Robbery', 'Shoplifting',
    'Theft from the person', 'Vehicle crime', 'Violence and sexual offences'
}

base_path = r'C:\ccview\site_selector\data\features\crime'
total = 0
errors = 0

sql = """
    INSERT INTO ingest.crime_raw
    (crime_id, month, force, longitude, latitude, lsoa21cd, lsoa_name, crime_type, outcome)
    VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s)
    ON CONFLICT DO NOTHING
"""

for month_dir in sorted(os.listdir(base_path)):
    month_path = os.path.join(base_path, month_dir)
    if not os.path.isdir(month_path):
        continue
    month_count = 0
    for fname in os.listdir(month_path):
        if not fname.endswith('.csv') or 'stop-and-search' in fname or 'outcomes' in fname:
            continue
        fpath = os.path.join(month_path, fname)
        try:
            with open(fpath, encoding='utf-8') as f:
                reader = csv.reader(f)
                next(reader)  # skip header
                batch = []
                for row in reader:
                    if len(row) < 10:
                        continue
                    crime_type = row[9].strip()
                    if crime_type not in CRIME_TYPES:
                        continue
                    lsoa = row[7].strip()
                    if not lsoa.startswith('E0'):
                        continue  # England only
                    batch.append((
                        row[0].strip() or None,
                        row[1].strip() or None,
                        row[2].strip() or None,
                        row[4].strip() or None,
                        row[5].strip() or None,
                        lsoa,
                        row[8].strip() or None,
                        crime_type,
                        row[10].strip() or None,
                    ))
                if batch:
                    cur.executemany(sql, batch)
                    conn.commit()
                    month_count += len(batch)
                    total += len(batch)
        except Exception as e:
            errors += 1
            conn.rollback()
            print(f'  ERROR {fname}: {e}')
            continue
    print(f'  {month_dir}: {month_count:,} rows')

cur.close()
conn.close()
print(f'\nTotal loaded: {total:,} rows | Errors: {errors}')