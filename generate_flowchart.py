import urllib.request
import zlib
import base64
import sys

def encode_plantuml(text):
    zlibbed_str = zlib.compress(text.encode('utf-8'))
    compressed_string = zlibbed_str[2:-4]
    return base64.b64encode(compressed_string).decode('utf-8').translate(
        str.maketrans('+/', '-_')
    )

puml_text = """
@startuml
!theme plain
title Flowchart Proses Pengaduan

start
:Warga Login;
:Warga Mengisi Form Pengaduan;
:Sistem Menyimpan Pengaduan (Status: BARU);
:Notifikasi terkirim ke Admin;
if (Admin Memvalidasi?) then (Ya)
  :Admin Mengubah Status (DIPROSES);
  :Petugas Menindaklanjuti;
  :Admin Mengubah Status (SELESAI);
else (Tidak/Ditolak)
  :Admin Mengubah Status (DITOLAK);
  :Admin Memberi Catatan Alasan;
endif
:Warga Menerima Notifikasi Status;
stop
@enduml
"""

try:
    encoded = encode_plantuml(puml_text)
    url = f'http://www.plantuml.com/plantuml/png/~1{encoded}'
    print(f'Fetching URL: {url}')
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    with urllib.request.urlopen(req) as response:
        content = response.read()
        print(f'Downloaded {len(content)} bytes')
        with open('image3.jpg', 'wb') as f:
            f.write(content)
    print('Generated image3.jpg successfully')
except Exception as e:
    print(f'Failed: {e}')
