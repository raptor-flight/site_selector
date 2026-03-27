import psycopg2
import csv

conn = psycopg2.connect(
    host="localhost", port=5432,
    database="proposiq", user="proposiq", password="proposiq_dev_pw"
)
cur = conn.cursor()

print('Loading LSOA to LAUA lookup...')
seen = set()
count = 0

with open(r'C:\ccview\site_selector\data\features\demographics\PCD_OA21_LSOA21_MSOA21_LAD_NOV25_UK_LU.csv', encoding='utf-8') as f:
    reader = csv.reader(f)
    next(reader)
    for row in reader:
        lsoa21cd = row[7].strip()
        ladcd    = row[9].strip()
        ladnm    = row[12].strip()
        if not lsoa21cd or lsoa21cd in seen:
            continue
        seen.add(lsoa21cd)
        cur.execute(
            "INSERT INTO ref.lsoa_to_laua (lsoa21cd, ladcd, ladnm) VALUES (%s,%s,%s) ON CONFLICT DO NOTHING",
            (lsoa21cd, ladcd, ladnm)
        )
        count += 1

conn.commit()
cur.close()
conn.close()
print(f'Loaded {count} rows')