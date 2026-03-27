import psycopg2
import csv
import zipfile
import io
import os

conn = psycopg2.connect(
    host="localhost", port=5432,
    database="proposiq", user="proposiq", password="proposiq_dev_pw"
)
cur = conn.cursor()

# --- 1. Fixed broadband coverage (postcode level from zip) ---
print('Loading fixed broadband coverage...')
zip_path = r'C:\ccview\site_selector\data\features\connectivity\fixed_broadband_coverage_full_fibre_ take-up\202507_fixed_coverage_r01\202507_fixed_pc_coverage_r01.zip'
count = 0
z = zipfile.ZipFile(zip_path)
for name in z.namelist():
    if '/postcode_files/' in name and name.endswith('.csv'):
        with z.open(name) as f:
            reader = csv.reader(io.TextIOWrapper(f, encoding='utf-8'))
            next(reader)
            for row in reader:
                if len(row) < 17:
                    continue
                postcode = row[0].strip().replace(' ', '').upper()
                if not postcode:
                    continue
                def val(x):
                    v = row[x].strip() if len(row) > x else None
                    return v if v else None
                try:
                    cur.execute("""
                        INSERT INTO ingest.ofcom_fixed_coverage_raw
                        (postcode, pct_sfbb, pct_ufbb_100, pct_ufbb, pct_gigabit, pct_below_uso, pct_nga)
                        VALUES (%s,%s,%s,%s,%s,%s,%s)
                        ON CONFLICT DO NOTHING
                    """, (postcode, val(9), val(10), val(11), val(16), val(17), val(18)))
                    count += 1
                except Exception as e:
                    conn.rollback()
                    continue
conn.commit()
print(f'  Loaded {count} postcode rows')

# --- 2. Fixed broadband performance (OA level) ---
print('Loading fixed broadband performance...')
perf_path = r'C:\ccview\site_selector\data\features\connectivity\fixed_broadband_performance\202507_fixed_performance_r01\202507_fixed_performance_oa_r01.csv'
count = 0
with open(perf_path, encoding='utf-8') as f:
    reader = csv.reader(f)
    next(reader)
    for row in reader:
        if len(row) < 7:
            continue
        oa = row[0].strip()
        if not oa:
            continue
        def val(x):
            v = row[x].strip() if len(row) > x else None
            return v if v else None
        try:
            cur.execute("""
                INSERT INTO ingest.ofcom_fixed_performance_raw
                (output_area, avg_dl_under_10, avg_dl_10_30, avg_dl_30_100,
                 avg_dl_100_300, avg_dl_300_900, avg_dl_900_plus)
                VALUES (%s,%s,%s,%s,%s,%s,%s)
                ON CONFLICT DO NOTHING
            """, (oa, val(1), val(2), val(3), val(4), val(5), val(6)))
            count += 1
        except Exception as e:
            conn.rollback()
            continue
conn.commit()
print(f'  Loaded {count} OA rows')

# --- 3. Mobile coverage (LAUA level) ---
print('Loading mobile coverage...')
mob_path = r'C:\ccview\site_selector\data\features\connectivity\mobile_coverage\202507_mobile_coverage_r01\202507_mobile_coverage_4G5G_r01.csv'
count = 0
with open(mob_path, encoding='utf-8-sig') as f:
    reader = csv.reader(f)
    next(reader)
    for row in reader:
        if len(row) < 10:
            continue
        ladcd = row[1].strip()
        if not ladcd.startswith('E0'):
            continue
        def val(x):
            v = row[x].strip() if len(row) > x else None
            return v if v else None
        try:
            cur.execute("""
                INSERT INTO ingest.ofcom_mobile_raw
                (ladcd, ladnm, fg_4g5g_prem_any, fg_4g5g_prem_all,
                 fg_4g5g_geo_any, fg_4g5g_geo_all)
                VALUES (%s,%s,%s,%s,%s,%s)
                ON CONFLICT DO NOTHING
            """, (ladcd, row[2].strip(), val(3), val(5), val(7), val(9)))
            count += 1
        except Exception as e:
            conn.rollback()
            continue
conn.commit()
print(f'  Loaded {count} LAUA rows')

cur.close()
conn.close()
print('\nAll done!')