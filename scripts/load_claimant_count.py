import psycopg2
import openpyxl

conn = psycopg2.connect(
    host="localhost", port=5432,
    database="proposiq", user="proposiq", password="proposiq_dev_pw"
)
cur = conn.cursor()

path = r'C:\ccview\site_selector\data\features\economy\claimant_count_lsoa.xlsx'

wb = openpyxl.load_workbook(path)
ws = wb.active

sql = """
    UPDATE feat.area_economy ae
    SET claimant_count = %s,
        claimant_rate = ROUND((%s::NUMERIC / NULLIF(dem.total_population, 0) * 100), 2)
    FROM core.geo_area ga
    JOIN feat.area_demographics dem ON dem.geo_area_id = ga.geo_area_id
    WHERE ae.geo_area_id = ga.geo_area_id
    AND ga.external_code = %s
"""

count = 0
for row in ws.iter_rows(min_row=9, values_only=True):
    if not row[1] or not row[2]:
        continue
    lsoa = str(row[1]).strip()
    if not lsoa.startswith('E01'):
        continue
    try:
        claimant_count = int(row[2])
        cur.execute(sql, (claimant_count, claimant_count, lsoa))
        if cur.rowcount > 0:
            count += 1
    except Exception as e:
        continue

conn.commit()
cur.close()
conn.close()
print(f'Updated {count:,} LSOAs with claimant count')