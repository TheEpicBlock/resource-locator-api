import os
import random
import shutil
import sys
from pathlib import Path
import hashlib
import subprocess
import urllib.request

def main():
    build_dir = Path(__file__).parent / "build"
    test_dir = build_dir / "testrunner"
    test_dir.mkdir(parents=True, exist_ok=True)

    if "clean" in sys.argv:
        shutil.rmtree(test_dir)
        return

    subprocess.run(["./gradlew", "testrunnerJar"], check=True)
    subprocess.run(["./gradlew", "jarWithGenericName"], check=True)

    # A test consists of a set of files, which form a minecraft server
    # The server is then run, and the output from the test runner will be checked
    # Files are hashed, but you can set the hash to `None` and it'll tell you the hash

    # Some commonly used files
    fabric_minecraft = {
        "url": "https://meta.fabricmc.net/v2/versions/loader/26.1.2/0.19.2/1.1.1/server/jar",
        "hash": "e91bd19b9ff0e04c57843e624d2f54d0a3e39a048cafca5e8e20eb9843445dbd",
        "target": "server.jar",
    }
    testrunner = {
        "local": build_dir / "libs" / "testrunner.jar",
        "target": "mods/testrunner.jar",
    }
    rla = {
        "local": build_dir / "libs" / "resource-locator.jar",
        "target": "mods/resource-locator.jar",
    }
    fapi = {
        "url": "https://cdn.modrinth.com/data/P7dR8mSH/versions/Sy2Bq7Xc/fabric-api-0.149.0%2B26.1.2.jar?mr_download_reason=standalone",
        "hash": "8318838738b3b0f0d11a99d6011e59c3b385f3958c8d0856503851130026f02e",
        "target": "mods/fabric.jar",
    }

    tests = {
        "basic_test": {
            "files": [
                fabric_minecraft,
                testrunner,
                rla,
                fapi,
            ],
            "results": {
                "basic:test.txt": [ "testfile123" ],
            }
        },
        "no_fapi": {
            "files": [
                fabric_minecraft,
                testrunner,
                rla,
            ],
            "results": {
                "basic:test.txt": [ "testfile123" ],
            }
        }
    }

    print("Downloading files")
    download_success = True
    for (name, test) in tests.items():
        instance_dir = test_dir / name
        instance_dir.mkdir(exist_ok=True)
        for file in test["files"]:
            if "local" in file:
                (instance_dir / file["target"]).parent.mkdir(parents=True,exist_ok=True)
                shutil.copy(file["local"], instance_dir / file["target"])
            elif "url" in file:
                success = download_file(file["url"], instance_dir / file["target"], file["hash"])
                if not success:
                    download_success = False
    if not download_success:
        sys.exit(1)
    if not "JAVA_HOME" in os.environ:
        print(f"No JAVA_HOME")
        sys.exit(1)
    java = Path(os.environ["JAVA_HOME"]) / "bin" / "java"
    for (name, test) in tests.items():
        instance_dir = test_dir / name
        print(f"Running {name}")
        env = {
            "TESTRUNNER_EXPECTS": ";".join([f"{k}={",".join(v)}" for k,v in test["results"].items()]),
            "TESTRUNNER_NONCE": str(random.randint(0,99999999))
        }
        subprocess.run([java, "-jar", instance_dir / "server.jar"], cwd=instance_dir, timeout=60*10, env=env)
        if not (instance_dir / "runner_result").exists() or read_file(instance_dir / "runner_result") != env["TESTRUNNER_NONCE"]:
            print("Test failed")
            sys.exit(1)
    print("All tests run successfully")

def download_file(url, target: Path, expected_hash: str | None) -> bool:
    while True:
        fresh = False
        if not target.exists():
            urllib.request.urlretrieve(url, target)
            fresh = True
        hash = hash_file(target)
        if expected_hash is None:
            print(f"{url}: {hash}")
            return False
        else:
            if hash != expected_hash:
                if not fresh:
                    os.unlink(target)
                    continue
                print(f"{target} expected {expected_hash}, was {hash}")
                return False
            return True
        raise Exception("Controlflow should not reach this point")

def hash_file(file: Path) -> str:
    hash_func = hashlib.new("sha256")
    with open(file, 'rb') as file:
        while chunk := file.read(8192):
            hash_func.update(chunk)
    return hash_func.hexdigest()

def read_file(file: Path) -> str:
    with open(file, "r") as f:
        return f.read()

if __name__ == "__main__":
    main()