import os

base = r'C:\ccview\site_selector\data\features\schools'
years = ['2018-2019', '2019-2020', '2020-2021', '2021-2022', '2022-2023', '2023-2024', '2024-2025']
files = [
    'england_school_information.csv',
    'england_ks2final.csv',
    'england_ks4final.csv',
    'england_ks5final.csv',
    'england_abs.csv',
    'england_census.csv'
]

for year in years:
    folder = os.path.join(base, year)
    if not os.path.exists(folder):
        print(f'Skipping {year} — folder not found')
        continue
    for f in files:
        path = os.path.join(folder, f)
        if not os.path.exists(path):
            print(f'  Skipping {year}/{f} — file not found')
            continue
        with open(path, 'r', encoding='utf-8-sig') as fin:
            content = fin.read()
        with open(path, 'w', encoding='utf-8') as fout:
            fout.write(content)
        print(f'  Fixed: {year}/{f}')

print('All done!')