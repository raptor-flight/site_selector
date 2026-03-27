import os
import zipfile
import subprocess
import tempfile
import shutil

# Configuration
DOCKER_CONTAINER = "proposiq-postgis"
PG_CONN = "PG:host=localhost dbname=proposiq user=proposiq password=proposiq_dev_pw"

DATASETS = [
    #{
    #    "name": "RoFRS",
    #    "base_path": r"C:\ccview\site_selector\data\features\flood_risk\nafra2\RoFRS",
    #    "table": "ingest.nafra2_rofrs_raw",
    #    "layer": "RoFRS_4band",
    #    "exclude": "Climate",
    #},
    {
        "name": "RoFSW",
        "base_path": r"C:\ccview\site_selector\data\features\flood_risk\nafra2\RoFSW\RoFSW",
        "table": "ingest.nafra2_rofsw_raw",
        "layer": "RoFSW",
        "exclude": "Climate",
    },
]

def get_zips(base_path, exclude):
    zips = []
    for root, dirs, files in os.walk(base_path):
        for f in files:
            if f.endswith(".zip") and exclude not in f:
                zips.append(os.path.join(root, f))
    return sorted(zips)

def load_zip(zip_path, table, layer, dataset_name, idx, total):
    tmp_dir = tempfile.mkdtemp()
    try:
        # Extract zip
        with zipfile.ZipFile(zip_path, 'r') as z:
            z.extractall(tmp_dir)

        # Find the .gdb folder
        gdb_path = None
        for item in os.listdir(tmp_dir):
            if item.endswith(".gdb"):
                gdb_path = os.path.join(tmp_dir, item)
                break

        if not gdb_path:
            print(f"  [{idx}/{total}] No GDB found in {zip_path}")
            return 0

        gdb_name = os.path.basename(gdb_path)
        docker_gdb = f"/tmp/{gdb_name}"

        # Check layer exists
        ogrinfo_result = subprocess.run(
            ["docker", "exec", DOCKER_CONTAINER, "ogrinfo", "-q", docker_gdb],
            capture_output=True, text=True
        )

        # Copy GDB into Docker
        subprocess.run(
            ["docker", "cp", gdb_path, f"{DOCKER_CONTAINER}:{docker_gdb}"],
            check=True, capture_output=True
        )

        # Run ogr2ogr inside Docker
        result = subprocess.run([
            "docker", "exec", DOCKER_CONTAINER,
            "ogr2ogr",
            "-f", "PostgreSQL",
            PG_CONN,
            docker_gdb,
            layer,
            "-nln", table,
            "-nlt", "PROMOTE_TO_MULTI",
            "-lco", "GEOMETRY_NAME=geom",
            "-append",
            "--config", "OGR_TRUNCATE", "NO",
            "-sql", f"SELECT Risk_band, Confidence, Shape_Area FROM {layer}",
        ], capture_output=True, text=True)

        if result.returncode != 0:
            print(f"  [{idx}/{total}] ERROR {gdb_name}: {result.stderr[:200]}")
            return 0

        # Clean up Docker temp
        subprocess.run(
            ["docker", "exec", DOCKER_CONTAINER, "rm", "-rf", docker_gdb],
            capture_output=True
        )

        tile = os.path.basename(zip_path).replace(".zip", "")
        print(f"  [{idx}/{total}] ✓ {tile}")
        return 1

    except Exception as e:
        print(f"  [{idx}/{total}] EXCEPTION {zip_path}: {e}")
        return 0
    finally:
        shutil.rmtree(tmp_dir, ignore_errors=True)

# Main
for dataset in DATASETS:
    print(f"\n{'='*50}")
    print(f"Loading {dataset['name']}...")
    print(f"{'='*50}")

    zips = get_zips(dataset["base_path"], dataset["exclude"])
    print(f"Found {len(zips)} zip files")

    success = 0
    for i, zip_path in enumerate(zips, 1):
        success += load_zip(
            zip_path,
            dataset["table"],
            dataset["layer"],
            dataset["name"],
            i,
            len(zips)
        )

    print(f"\n{dataset['name']}: {success}/{len(zips)} tiles loaded successfully")

print("\nAll done!")