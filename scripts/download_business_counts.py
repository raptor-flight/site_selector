import urllib.request

# Claimant count at LSOA level - NM_162_1
url = "https://www.nomisweb.co.uk/api/v01/dataset/NM_162_1.data.csv?geography=TYPE298&date=latest&gender=0&age=0&measures=20100"

output = r'C:\ccview\site_selector\data\features\economy\claimant_count_lsoa.csv'
print('Downloading claimant count at LSOA...')
urllib.request.urlretrieve(url, output)

import csv
with open(output, encoding='utf-8') as f:
    lines = f.readlines()
print(f'Total lines: {len(lines)}')
print('First 3:')
for line in lines[:3]:
    print(line.strip()[:200])