import psycopg2
import csv

conn = psycopg2.connect(
    host="localhost", port=5432,
    database="proposiq", user="proposiq", password="proposiq_dev_pw"
)
cur = conn.cursor()

path = r'C:\ccview\site_selector\data\features\economy\job_density_laua_2023.csv'

sql = "INSERT INTO ingest.job_density_laua_raw (ladnm, job_density) VALUES (%s,%s)"

count = 0
with open(path, encoding='utf-8') as f:
    lines = f.readlines()

for line in lines[7:]:
    line = line.strip()
    if not line:
        continue
    row = list(csv.reader([line]))[0]
    if len(row) < 2 or not row[1].strip():
        continue
    try:
        jd = row[1].strip()
        if not jd or jd == '-' or not jd.replace('.','').isdigit():
            continue
        cur.execute(sql, (row[0].strip(), jd))
        count += 1
    except Exception as e:
        print(f'ERROR: {e}')
        conn.rollback()
        continue

conn.commit()
cur.close()
conn.close()
print(f'Loaded {count:,} job density rows')