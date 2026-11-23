"""Cliente de prueba de la semana 15 para ChipZone.

Pide la clave sin mostrarla, conserva el JWT solo en memoria y muestra unicamente estados HTTP.
Requiere Python 3 y el servidor local iniciado (perfil lab). No pegue el token en sitios externos.
"""
import getpass
import json
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen

BASE = "http://127.0.0.1:8080"


def request(path, method="GET", payload=None, token=None):
    headers = {"Accept": "application/json"}
    body = None
    if payload is not None:
        headers["Content-Type"] = "application/json"
        body = json.dumps(payload).encode("utf-8")
    if token is not None:
        headers["Authorization"] = "Bearer " + token
    req = Request(BASE + path, data=body, headers=headers, method=method)
    try:
        with urlopen(req, timeout=5) as response:
            return response.status, response.read()
    except HTTPError as error:
        return error.code, error.read()


try:
    name = input("Usuario de laboratorio: ")
    password = getpass.getpass("Contraseña: ")
    status, body = request("/api/v1/auth/token", "POST",
                           {"username": name, "password": password})
    del password
    if status != 200:
        raise SystemExit(f"No se emitió el token. HTTP {status}")
    token = json.loads(body)["access_token"]

    status, _ = request("/api/v1/productos")
    print("GET sin token:", status, "(esperado 200: la lectura es pública)")
    status, _ = request("/api/v1/productos", "POST", {})
    print("POST vacío sin token:", status, "(esperado 401)")
    status, _ = request("/api/v1/productos", "POST", {}, token)
    print("POST vacío con token:", status, "(USER: 403; ADMIN: 400)")
    del token
except URLError:
    raise SystemExit("No se pudo conectar al servidor local")
