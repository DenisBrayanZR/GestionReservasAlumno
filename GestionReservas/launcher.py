#!/usr/bin/env python3
"""Mini "docker-compose" en Python para arrancar la practica (sin tocar el proyecto).

Uso:
    python launcher.py check    # comprueba requisitos
    python launcher.py up       # MySQL (si hace falta) + API Spring Boot + frontend + navegador
    python launcher.py status   # estado de los servicios
    python launcher.py down     # para API y frontend

Configuracion por variables de entorno (opcionales):
    DB_URL, DB_USERNAME, DB_PASSWORD, JAVA_HOME, MYSQL_SERVICE (def. MySQL80)
"""
import glob
import json
import os
import shutil
import socket
import subprocess
import sys
import time
import webbrowser
from pathlib import Path

ROOT = Path(__file__).resolve().parent
APP = ROOT / "ReservasAulas"
FRONT = APP / "frontend"
STATE = ROOT / ".launcher_state.json"
LOGS = ROOT / "launcher_logs"

API_PORT, FRONT_PORT, DB_PORT = 8080, 5500, 3306
MYSQL_SERVICE = os.environ.get("MYSQL_SERVICE", "MySQL80")
IS_WIN = os.name == "nt"


def port_open(port, host="127.0.0.1"):
    with socket.socket() as s:
        s.settimeout(0.5)
        return s.connect_ex((host, port)) == 0


def find_maven():
    """mvnw si el wrapper esta completo; si no, Maven del PATH o de gvs-tools."""
    if (APP / ".mvn" / "wrapper").exists():
        return str(APP / ("mvnw.cmd" if IS_WIN else "mvnw"))
    m = shutil.which("mvn") or shutil.which("mvn.cmd")
    if m:
        return m
    for c in glob.glob(str(Path.home() / "dev" / "gvs-tools" / "apache-maven-*" / "bin" / "mvn.cmd")):
        return c
    sys.exit("ERROR: no hay Maven (ni .mvn/wrapper). Instala Maven o anade mvn al PATH.")


def wait_port(port, timeout, name, proc=None):
    print(f"  esperando a {name} (puerto {port})", end="", flush=True)
    end = time.time() + timeout
    while time.time() < end:
        if port_open(port):
            print(" OK")
            return True
        if proc is not None and proc.poll() is not None:
            print(" el proceso ha terminado")
            return False
        print(".", end="", flush=True)
        time.sleep(2)
    print(" TIMEOUT")
    return False


def find_java_home():
    """Busca un JDK: JAVA_HOME, PATH, IntelliJ (jbr) y rutas habituales."""
    jh = os.environ.get("JAVA_HOME")
    if jh and Path(jh, "bin", "java.exe" if IS_WIN else "java").exists():
        return jh
    j = shutil.which("java")
    if j:
        return str(Path(j).resolve().parent.parent)
    pats = [
        r"C:\Program Files\Java\*", r"C:\Program Files\Eclipse Adoptium\*",
        r"C:\Program Files\Microsoft\jdk*", r"C:\Program Files\Amazon Corretto\*",
        r"C:\Program Files\JetBrains\*\jbr",
        str(Path.home() / ".jdks" / "*"),
        str(Path.home() / "AppData/Local/Programs/JetBrains/*/jbr"),
        str(Path.home() / "AppData/Local/JetBrains/Toolbox/apps/*/*/*/jbr"),
    ]
    for p in pats:
        for c in sorted(glob.glob(p), reverse=True):
            if Path(c, "bin", "java.exe").exists():
                return c
    return None


def find_mysqld():
    m = shutil.which("mysqld")
    if m:
        return m
    for c in glob.glob(r"C:\Program Files\MySQL\MySQL Server *\bin\mysqld.exe"):
        return c
    return None


def check(verbose=True):
    ok = True
    jh = find_java_home()
    items = [
        ("Python", sys.version.split()[0], True),
        ("Java (JDK)", jh or "NO ENCONTRADO (instala JDK o define JAVA_HOME)", bool(jh)),
        ("mvnw.cmd", str(APP / "mvnw.cmd"), (APP / "mvnw.cmd").exists()),
        ("Frontend", str(FRONT), (FRONT / "index.html").exists()),
        ("MySQL en :3306", "activo" if port_open(DB_PORT) else "apagado (se intentara arrancar)", True),
    ]
    for n, v, good in items:
        if verbose:
            print(f"[{'OK' if good else 'XX'}] {n}: {v}")
        ok &= good
    return ok, jh


def ensure_mysql():
    if port_open(DB_PORT):
        print("MySQL ya esta en marcha.")
        return True
    if IS_WIN:
        print(f"Arrancando servicio de Windows '{MYSQL_SERVICE}'...")
        r = subprocess.run(["net", "start", MYSQL_SERVICE], capture_output=True, text=True)
        if r.returncode != 0:
            print("  No se pudo (puede requerir terminal como Administrador):",
                  (r.stdout + r.stderr).strip().splitlines()[-1:] or "")
    if not port_open(DB_PORT) and not wait_port(DB_PORT, 30, "MySQL"):
        print("ERROR: MySQL no responde. Inicialo manualmente (XAMPP/Workbench/servicio) y repite.")
        return False
    return True


def spawn(cmd, cwd, log, env=None):
    LOGS.mkdir(exist_ok=True)
    f = open(LOGS / log, "w", encoding="utf-8", errors="replace")
    flags = subprocess.CREATE_NEW_PROCESS_GROUP if IS_WIN else 0
    return subprocess.Popen(cmd, cwd=cwd, stdout=f, stderr=subprocess.STDOUT, env=env, creationflags=flags)


def save_state(**kw):
    data = json.loads(STATE.read_text()) if STATE.exists() else {}
    data.update(kw)
    STATE.write_text(json.dumps(data))


def up():
    ok, jh = check()
    if not ok:
        return 1
    if not ensure_mysql():
        return 1

    if port_open(API_PORT):
        print(f"La API ya responde en :{API_PORT}, no la relanzo.")
    else:
        env = os.environ.copy()
        env["JAVA_HOME"] = jh
        mvn = find_maven()
        print(f"Arrancando API Spring Boot con {mvn} (log: launcher_logs/api.log)...")
        p = spawn([mvn, "spring-boot:run"], APP, "api.log", env)
        save_state(api_pid=p.pid)
        if not wait_port(API_PORT, 240, "API", p):
            print("ERROR: la API no arranco. Ultimas lineas de launcher_logs/api.log:")
            tail = (LOGS / "api.log").read_text(encoding="utf-8", errors="replace").splitlines()[-25:]
            print("\n".join(tail))
            return 1

    if port_open(FRONT_PORT):
        print(f"Frontend ya servido en :{FRONT_PORT}.")
    else:
        print("Sirviendo frontend...")
        p = spawn([sys.executable, "-m", "http.server", str(FRONT_PORT)], FRONT, "frontend.log")
        save_state(front_pid=p.pid)
        wait_port(FRONT_PORT, 10, "frontend")

    print(f"\nListo:\n  API      http://localhost:{API_PORT}\n  Frontend http://localhost:{FRONT_PORT}")
    webbrowser.open(f"http://localhost:{FRONT_PORT}")
    return 0


def kill_tree(pid):
    if IS_WIN:
        subprocess.run(["taskkill", "/PID", str(pid), "/T", "/F"], capture_output=True)
    else:
        try:
            os.kill(pid, 15)
        except OSError:
            pass


def down():
    if STATE.exists():
        for k, pid in json.loads(STATE.read_text()).items():
            print(f"Parando {k} (pid {pid})")
            kill_tree(pid)
        STATE.unlink()
    else:
        print("Nada que parar (no hay estado guardado).")
    print("MySQL se deja encendido.")
    return 0


def status():
    for n, p in (("MySQL", DB_PORT), ("API", API_PORT), ("Frontend", FRONT_PORT)):
        print(f"{n:9} :{p}  {'UP' if port_open(p) else 'DOWN'}")
    return 0


if __name__ == "__main__":
    cmd = sys.argv[1] if len(sys.argv) > 1 else "up"
    actions = {"check": lambda: 0 if check()[0] else 1, "up": up, "down": down, "status": status}
    if cmd not in actions:
        print(__doc__)
        sys.exit(2)
    sys.exit(actions[cmd]())
