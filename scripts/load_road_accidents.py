import psycopg2
import csv

conn = psycopg2.connect(
    host="localhost", port=5432,
    database="proposiq", user="proposiq", password="proposiq_dev_pw"
)
cur = conn.cursor()

path = r'C:\ccview\site_selector\data\features\road-traffic-accidents\dft-road-casualty-statistics-collision-last-5-years.csv'

sql = """
    INSERT INTO ingest.road_accidents_raw
    (collision_index, collision_year, longitude, latitude,
     collision_severity, number_of_casualties, number_of_vehicles,
     speed_limit, urban_or_rural, lsoa21cd)
    VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
    ON CONFLICT DO NOTHING
"""

def val(row, idx):
    v = row[idx].strip() if len(row) > idx else None
    return v if v else None

count = 0
errors = 0
batch = []

print('Loading road accidents...')
with open(path, encoding='utf-8') as f:
    reader = csv.reader(f)
    next(reader)
    for row in reader:
        lsoa = val(row, 39)
        if not lsoa or not lsoa.startswith('E0'):
            continue
        batch.append((
            val(row, 0),   # collision_index
            val(row, 1),   # collision_year
            val(row, 5),   # longitude
            val(row, 6),   # latitude
            val(row, 8),   # collision_severity
            val(row, 10),  # number_of_casualties
            val(row, 9),   # number_of_vehicles
            val(row, 21),  # speed_limit
            val(row, 36),  # urban_or_rural
            lsoa,
        ))
        if len(batch) >= 10000:
            try:
                cur.executemany(sql, batch)
                conn.commit()
                count += len(batch)
            except Exception as e:
                errors += 1
                conn.rollback()
            batch = []

if batch:
    cur.executemany(sql, batch)
    conn.commit()
    count += len(batch)

cur.close()
conn.close()
print(f'Loaded {count:,} rows | Errors: {errors}')