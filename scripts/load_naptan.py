import psycopg2
import csv

conn = psycopg2.connect(
    host="localhost", port=5432,
    database="proposiq", user="proposiq", password="proposiq_dev_pw"
)
cur = conn.cursor()

WANTED_TYPES = {'BCT', 'RLY', 'MET', 'FER', 'AIR'}

path = r'C:\ccview\site_selector\data\features\transport\naptan\Stops.csv'

sql = """
    INSERT INTO ingest.naptan_raw
    (atco_code, common_name, longitude, latitude, stop_type, status)
    VALUES (%s,%s,%s,%s,%s,%s)
    ON CONFLICT DO NOTHING
"""

count = 0
with open(path, encoding='utf-8') as f:
    reader = csv.reader(f)
    next(reader)
    batch = []
    for row in reader:
        if len(row) < 42:
            continue
        stop_type = row[31].strip()
        if stop_type not in WANTED_TYPES:
            continue
        status = row[42].strip() if len(row) > 42 else ''
        if status != 'active':
            continue
        lng = row[29].strip()
        lat = row[30].strip()
        if not lng or not lat:
            continue
        batch.append((
            row[0].strip(),   # ATCOCode
            row[4].strip(),   # CommonName
            lng,              # Longitude
            lat,              # Latitude
            stop_type,
            status,
        ))
        if len(batch) >= 5000:
            cur.executemany(sql, batch)
            conn.commit()
            count += len(batch)
            batch = []

if batch:
    cur.executemany(sql, batch)
    conn.commit()
    count += len(batch)

cur.close()
conn.close()
print(f'Loaded {count:,} NaPTAN stops')