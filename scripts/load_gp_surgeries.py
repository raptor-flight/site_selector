import psycopg2
import csv

conn = psycopg2.connect(
    host="localhost", port=5432,
    database="proposiq", user="proposiq", password="proposiq_dev_pw"
)
cur = conn.cursor()

path = r'C:\ccview\site_selector\data\features\health\gp_surgeries\epraccur.csv'

sql = """
    INSERT INTO ingest.gp_surgeries_raw (practice_code, practice_name, postcode, status)
    VALUES (%s,%s,%s,%s) ON CONFLICT DO NOTHING
"""

count = 0
with open(path, encoding='utf-8') as f:
    reader = csv.reader(f)
    for row in reader:
        if len(row) < 13:
            continue
        if row[12].strip() != 'ACTIVE':
            continue
        postcode = row[9].strip().replace(' ', '').upper()
        if not postcode:
            continue
        cur.execute(sql, (
            row[0].strip(),
            row[1].strip(),
            postcode,
            row[12].strip()
        ))
        count += 1

conn.commit()
cur.close()
conn.close()
print(f'Loaded {count:,} active GP surgeries')