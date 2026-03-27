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

def load_census_file(path, table, col_map, skip_rows=8):
    """
    col_map = list of (table_column_name, csv_column_index)
    skip_rows = number of header rows to skip (Nomis files have 8)
    """
    print(f'Loading {table} from {path}...')
    table_cols = [c[0] for c in col_map]
    col_indexes = [c[1] for c in col_map]

    col_str = ', '.join(table_cols)
    placeholders = ', '.join(['%s'] * len(table_cols))
    sql = f"INSERT INTO {table} ({col_str}) VALUES ({placeholders}) ON CONFLICT DO NOTHING"

    count = 0
    with open(path, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    # Find data rows — skip header block and blank lines
    data_lines = []
    for line in lines[skip_rows:]:
        line = line.strip()
        if line and not line.startswith('"Date') and not line.startswith('"Population'):
            data_lines.append(line)

    reader = csv.reader(data_lines)
    for row in reader:
        if not row or not row[1].strip().startswith('E02'):
            continue
        values = []
        for idx in col_indexes:
            val = row[idx].strip() if len(row) > idx else None
            val = val.strip('"') if val else None
            values.append(val if val else None)
        try:
            cur.execute(sql, values)
            count += 1
        except Exception as e:
            print(f'  ROW ERROR: {e} — {values[:2]}')
            conn.rollback()
            continue
    conn.commit()
    print(f'  Loaded {count} rows')

# TS044 — Accommodation type
load_census_file(
    r'C:\ccview\site_selector\data\features\accommodation_type_ts044\944860827450184.csv',
    'ingest.census_ts044_raw',
    [
        ('msoa21cd',1), ('total_households',2),
        ('detached',4), ('semi_detached',6), ('terraced',8),
        ('flat_purpose_built',10), ('flat_converted',12),
        ('flat_other',14), ('commercial_building',16), ('mobile_temporary',18)
    ]
)

# TS054 — Tenure
load_census_file(
    r'C:\ccview\site_selector\data\features\tenure_ts054\16913551211451661.csv',
    'ingest.census_ts054_raw',
    [
        ('msoa21cd',1), ('total_households',2),
        ('owned',4), ('owned_outright',6), ('owned_mortgage',8),
        ('shared_ownership',10),
        ('social_rented',14), ('social_rented_council',16), ('social_rented_other',18),
        ('private_rented',20), ('private_rented_landlord',22), ('private_rented_other',24),
        ('lives_rent_free',26)
    ]
)

# TS021 — Ethnic group
load_census_file(
    r'C:\ccview\site_selector\data\features\ethnic_group_ts021\943466120940580.csv',
    'ingest.census_ts021_raw',
    [
        ('msoa21cd',1), ('total_population',2),
        ('asian_total',4), ('asian_bangladeshi',6), ('asian_chinese',8),
        ('asian_indian',10), ('asian_pakistani',12), ('asian_other',14),
        ('black_total',16), ('black_african',18), ('black_caribbean',20), ('black_other',22),
        ('mixed_total',24), ('mixed_white_asian',26), ('mixed_white_black_african',28),
        ('mixed_white_black_caribbean',30), ('mixed_other',32),
        ('white_total',34), ('white_british',36), ('white_irish',38),
        ('white_gypsy',40), ('white_roma',42), ('white_other',44),
        ('other_total',46), ('other_arab',48), ('other_any',50)
    ]
)

# TS066 — Economic activity
load_census_file(
    r'C:\ccview\site_selector\data\features\economic_activity_status_ts066\2553791642915247.csv',
    'ingest.census_ts066_raw',
    [
        ('msoa21cd',1), ('total_aged_16_over',2),
        ('economically_active',4), ('in_employment',6),
        ('employee_part_time',10), ('employee_full_time',12),
        ('self_employed',14), ('unemployed',26),
        ('economically_inactive',52), ('inactive_retired',54),
        ('inactive_student',56), ('inactive_looking_after_home',58),
        ('inactive_long_term_sick',60), ('inactive_other',62)
    ]
)

cur.close()
conn.close()
print('\nAll done!')