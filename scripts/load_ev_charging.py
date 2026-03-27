import psycopg2
from odf.opendocument import load
from odf.table import Table, TableRow, TableCell
from odf.text import P

conn = psycopg2.connect(
    host="localhost", port=5432,
    database="proposiq", user="proposiq", password="proposiq_dev_pw"
)
cur = conn.cursor()

cur.execute("""
    CREATE TABLE IF NOT EXISTS ingest.ev_charging_laua_raw (
        ladcd           VARCHAR(9),
        ladnm           VARCHAR(100),
        ev_charger_count INTEGER
    );
    CREATE INDEX IF NOT EXISTS idx_ev_charging_lad 
        ON ingest.ev_charging_laua_raw(ladcd);
""")
conn.commit()

path = r'C:\Users\kuldipbajwa\Downloads\evci0102_2026-01_EV_chargers_by_local_authority_and_county_UK.ods'

doc = load(path)
sheets = doc.spreadsheet.getElementsByType(Table)


cur.execute("DELETE FROM ingest.ev_charging_laua_raw")
conn.commit()

count = 0
for sheet in sheets:
    if sheet.getAttribute('name') == 'EVCI0102a':
        rows = sheet.getElementsByType(TableRow)
        for row in rows[3:]:
            cells = row.getElementsByType(TableCell)
            vals = [''.join(str(p) for p in cell.getElementsByType(P)) for cell in cells]
            if len(vals) >= 5 and vals[0].startswith('E'):
                charger_count = vals[4].strip()
                if charger_count and charger_count != '[x]':
                    try:
                        cur.execute(
                            "INSERT INTO ingest.ev_charging_laua_raw (ladcd, ladnm, ev_charger_count) VALUES (%s,%s,%s) ON CONFLICT DO NOTHING",
                            (vals[0], vals[1], int(charger_count.replace(',', '')))
                        )
                        count += 1
                    except Exception as e:
                        print(f'ERROR: {e}')
                        conn.rollback()

conn.commit()
cur.close()
conn.close()
print(f'Loaded {count} EV charging rows')