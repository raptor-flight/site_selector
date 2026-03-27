"C:\Users\kuldipbajwa\AppData\Local\Python\pythoncore-3.14-64\python.exe" -c "
import csv
files = [
    r'C:\ccview\site_selector\data\features\connectivity\fixed_broadband_performance\202507_fixed_performance_r01\202507_fixed_performance_oa_r01.csv',
    r'C:\ccview\site_selector\data\features\connectivity\mobile_coverage\202507_mobile_coverage_r01\202507_mobile_coverage_laua_r01.csv',
]
for f in files:
    print(f'=== {f.split(chr(92))[-1]} ===')
    with open(f, encoding='utf-8') as fp:
        r = csv.reader(fp)
        for i, h in enumerate(next(r)):
            print(f'  {i}: {h}')
    print()
"