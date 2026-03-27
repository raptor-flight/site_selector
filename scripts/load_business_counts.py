import psycopg2
import csv

conn = psycopg2.connect(
    host="localhost", port=5432,
    database="proposiq", user="proposiq", password="proposiq_dev_pw"
)
cur = conn.cursor()

path = r'C:\ccview\site_selector\data\features\economy\business_counts_msoa_by_sector.csv'

sql = """
    INSERT INTO ingest.business_counts_msoa_raw (
        msoa21cd, agriculture, mining_utilities, manufacturing, construction,
        motor_trades, wholesale, retail, transport, accommodation_food,
        information_comms, financial_insurance, property, professional,
        business_admin, public_admin, education, health, arts_other
    ) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
    ON CONFLICT DO NOTHING
"""

def clean(v):
    v = v.strip()
    return int(v) if v and v.isdigit() else None

count = 0
with open(path, encoding='utf-8') as f:
    lines = f.readlines()

for line in lines[9:]:
    line = line.strip()
    if not line:
        continue
    row = list(csv.reader([line]))[0]
    if len(row) < 19:
        continue
    # Extract MSOA code from "E02002483 : Hartlepool 001"
    msoa_field = row[0].strip()
    if not msoa_field.startswith('E02'):
        continue
    msoa21cd = msoa_field.split(' : ')[0].strip()
    try:
        cur.execute(sql, (
            msoa21cd,
            clean(row[1]),  clean(row[2]),  clean(row[3]),  clean(row[4]),
            clean(row[5]),  clean(row[6]),  clean(row[7]),  clean(row[8]),
            clean(row[9]),  clean(row[10]), clean(row[11]), clean(row[12]),
            clean(row[13]), clean(row[14]), clean(row[15]), clean(row[16]),
            clean(row[17]), clean(row[18]),
        ))
        count += 1
    except Exception as e:
        print(f'ERROR: {e} — {row[:3]}')
        conn.rollback()
        continue

conn.commit()
cur.close()
conn.close()
print(f'Loaded {count:,} MSOA business count rows')