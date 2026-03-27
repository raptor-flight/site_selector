import psycopg2
import csv

conn = psycopg2.connect(
    host="localhost",
    port=5432,
    database="proposiq",
    user="proposiq",
    password="proposiq_dev_pw"
)
cur = conn.cursor()

path = r'C:\ccview\site_selector\data\features\demographics\PCD_OA21_LSOA21_MSOA21_LAD_NOV25_UK_LU.csv'

sql = """
    INSERT INTO ref.lsoa_to_msoa (lsoa21cd, msoa21cd, msoa21nm)
    VALUES (%s, %s, %s)
    ON CONFLICT (lsoa21cd) DO NOTHING
"""

count = 0
seen = set()
with open(path, 'r', encoding='utf-8') as f:
    reader = csv.reader(f)
    next(reader)  # skip header
    for row in reader:
        lsoa = row[7].strip()
        msoa = row[8].strip()
        msoa_nm = row[11].strip()
        if lsoa and msoa and lsoa not in seen:
            seen.add(lsoa)
            cur.execute(sql, (lsoa, msoa, msoa_nm))
            count += 1
        if count % 10000 == 0:
            conn.commit()
            print(f'  {count} rows...')

conn.commit()
print(f'Done — {count} unique LSOAs loaded')
cur.close()
conn.close()