import psycopg2
import csv
import os

conn = psycopg2.connect(
    host="localhost",
    port=5432,
    database="proposiq",
    user="proposiq",
    password="proposiq_dev_pw"
)
cur = conn.cursor()

base = r'C:\ccview\site_selector\data\features\schools'

years = {
    '2018-2019': 2019,
    '2020-2021': 2021,
    '2021-2022': 2022,
    '2022-2023': 2023,
    '2023-2024': 2024,
    '2024-2025': 2025
}

def clean(val):
    if val is None:
        return None
    val = val.strip()
    if val.upper() in ('', 'NE', 'SUPP', 'NP', 'NA', 'NAT', 'NEW', 'LOWCOV', 'DNS'):
        return None
    # Strip percentage signs
    if val.endswith('%'):
        val = val[:-1]
    return val

def load_by_index(path, table, col_map, year):
    """
    col_map = list of (table_column_name, csv_column_index)
    """
    if not os.path.exists(path):
        print(f'  SKIP: {path} not found')
        return 0
    with open(path, 'r', encoding='utf-8') as f:
        reader = csv.reader(f)
        next(reader)  # skip header
        rows = list(reader)

    table_cols = [c[0] for c in col_map]
    col_indexes = [c[1] for c in col_map]

    col_str = ', '.join(table_cols + ['data_year'])
    placeholders = ', '.join(['%s'] * (len(table_cols) + 1))
    sql = f"INSERT INTO {table} ({col_str}) VALUES ({placeholders}) ON CONFLICT DO NOTHING"

    count = 0
    for row in rows:
        # Skip non-numeric URN rows
        first_val = row[col_indexes[0]].strip() if len(row) > col_indexes[0] else ''
        if not first_val.lstrip('-').isdigit():
            continue
        values = []
        for idx in col_indexes:
            val = clean(row[idx]) if len(row) > idx else None
            values.append(val)
        values.append(year)
        try:
            cur.execute(sql, values)
            count += 1
        except Exception as e:
            print(f'  ROW ERROR: {e} — {values[:3]}')
            conn.rollback()
            continue
    conn.commit()
    print(f'  Loaded {count} rows into {table} for year {year}')
    return count

for year_folder, year_int in years.items():
    print(f'\nProcessing {year_folder}...')
    folder = os.path.join(base, year_folder)

    # School information — columns 0-25
    load_by_index(
        os.path.join(folder, 'england_school_information.csv'),
        'ingest.schools_info_raw',
        [
            ('urn',0), ('laname',1), ('la',2), ('estab',3), ('laestab',4),
            ('schname',5), ('street',6), ('locality',7), ('address3',8),
            ('town',9), ('postcode',10), ('schstatus',11), ('opendate',12),
            ('closedate',13), ('minorgroup',14), ('schooltype',15),
            ('isprimary',16), ('issecondary',17), ('ispost16',18),
            ('agelow',19), ('agehigh',20), ('gender',21), ('relchar',22),
            ('admpol',23), ('ofstedrating',24), ('ofstedlastinsp',25)
        ],
        year_int
    )

    # Absence — URN at 0, absence at 3, persistent at 4
    load_by_index(
        os.path.join(folder, 'england_abs.csv'),
        'ingest.schools_abs_raw',
        [('urn',0), ('pct_overall_absence',3), ('pct_persistent_absence',4)],
        year_int
    )

    # Census — all 22 columns in order
    load_by_index(
        os.path.join(folder, 'england_census.csv'),
        'ingest.schools_census_raw',
        [
            ('urn',0), ('la',1), ('estab',2), ('schooltype',3),
            ('total_pupils',4), ('total_girls',5), ('total_boys',6),
            ('pct_girls',7), ('pct_boys',8), ('total_sen_ehc',9),
            ('pct_sen_ehc',10), ('total_sen_support',11), ('pct_sen_support',12),
            ('num_eal',13), ('num_eng_fl',14), ('num_unc_fl',15),
            ('pct_eal',16), ('pct_eng_fl',17), ('pct_unc_fl',18),
            ('num_fsm',19), ('num_fsm_ever',20), ('pct_fsm_ever',21)
        ],
        year_int
    )

    # KS2 — exact column positions from CSV
    load_by_index(
        os.path.join(folder, 'england_ks2final.csv'),
        'ingest.schools_ks2_raw',
        [
            ('urn',4), ('schname',5), ('postcode',10),
            ('total_pupils',24), ('pct_rwm_expected',48),
            ('pct_rwm_high',49), ('reading_progress',50),
            ('writing_progress',54), ('maths_progress',58)
        ],
        year_int
    )

    # KS4 — need exact positions, check first
    load_by_index(
        os.path.join(folder, 'england_ks4final.csv'),
        'ingest.schools_ks4_raw',
        [
            ('urn',3), ('schname',4), ('postcode',10),
            ('total_pupils',26), ('att8_score',64),
            ('progress8_score',73), ('progress8_lower',74),
            ('progress8_upper',75), ('pct_basics_94',88),
            ('pct_basics_95',89)
        ],
        year_int
    )

    # KS5 — need exact positions
    load_by_index(
        os.path.join(folder, 'england_ks5final.csv'),
        'ingest.schools_ks5_raw',
        [
            ('urn',6), ('schname',10), ('postcode',15),
            ('total_pupils_1618',30), ('alevel_entries',31),
            ('alevel_avg_grade',32)
        ],
        year_int
    )

cur.close()
conn.close()
print('\nAll done!')