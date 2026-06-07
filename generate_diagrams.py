import urllib.request
import urllib.parse
import zlib
import base64
import string

def encode_plantuml(text):
    zlibbed_str = zlib.compress(text.encode('utf-8'))
    compressed_string = zlibbed_str[2:-4]
    return base64.b64encode(compressed_string).decode('utf-8').translate(
        str.maketrans('+/', '-_')
    )

def download_diagram(puml_text, filename):
    encoded = encode_plantuml(puml_text)
    url = f"http://www.plantuml.com/plantuml/png/~1{encoded}"
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req) as response:
            with open(filename, 'wb') as f:
                f.write(response.read())
        print(f"Berhasil men-generate {filename}")
    except Exception as e:
        print(f"Gagal men-generate {filename}: {e}")

# 1. ERD
erd_puml = """
@startuml
!theme plain
entity "Users" as users {
  * id : UUID
  --
  name : VARCHAR
  email : VARCHAR
  password : VARCHAR
  role : VARCHAR
}

entity "Pengaduan" as pengaduan {
  * id : UUID
  --
  user_id : UUID <<FK>>
  judul : VARCHAR
  deskripsi : TEXT
  status : VARCHAR
}

entity "LayananAdministrasi" as layanan {
  * id : UUID
  --
  user_id : UUID <<FK>>
  kategori_id : INTEGER <<FK>>
  keperluan : TEXT
  status : VARCHAR
}

entity "KategoriLayanan" as kategori {
  * id : INTEGER
  --
  nama_kategori : VARCHAR
}

users ||--o{ pengaduan : "Membuat"
users ||--o{ layanan : "Mengajukan"
kategori ||--o{ layanan : "Memiliki"
@enduml
"""

# 2. Class Diagram
class_puml = """
@startuml
!theme plain
package "com.smartpelayanan.entity" {
  class User {
    - id: UUID
    - namaLengkap: String
    - email: String
    - role: RoleEnum
  }
  class Pengaduan {
    - id: UUID
    - judul: String
    - status: StatusPengaduanEnum
  }
  class LayananAdministrasi {
    - id: UUID
    - keperluan: String
    - status: StatusLayananEnum
  }
}

package "com.smartpelayanan.controller" {
  class PengaduanController
  class LayananController
  class AuthController
}

User "1" -- "0..*" Pengaduan
User "1" -- "0..*" LayananAdministrasi
PengaduanController --> Pengaduan
LayananController --> LayananAdministrasi
@enduml
"""

# 3. REST API Diagram
api_puml = """
@startuml
!theme plain
left to right direction
node "SmartPelayanan API (/api/v1)" {
  [Auth] --> (/auth/login)
  [Auth] --> (/auth/register)
  
  [Warga] --> (/warga/pengaduan)
  [Warga] --> (/warga/layanan)
  
  [Admin] --> (/admin/pengaduan)
  [Admin] --> (/admin/layanan)
  [Admin] --> (/admin/kategori)
}
@enduml
"""

download_diagram(erd_puml, "image2.jpg")
download_diagram(class_puml, "image4.jpg")
download_diagram(api_puml, "image5.jpg")
