---
name: plantuml-api-diagram-generation
description: Generate PlantUML diagrams via Python HTTP request to PlantUML server
source: auto-skill
extracted_at: '2026-06-07T04:36:29.740Z'
---

When generating PlantUML diagrams programmatically without a local PlantUML/Java installation, you can use the public PlantUML API.

### Key Learnings & 403 Forbidden Fix
1. **Encoding Algorithm:** PlantUML requires the text to be zlib compressed and then custom-base64 encoded.
   ```python
   import zlib
   import base64
   
   def encode_plantuml(text):
       zlibbed_str = zlib.compress(text.encode('utf-8'))
       compressed_string = zlibbed_str[2:-4]
       return base64.b64encode(compressed_string).decode('utf-8').translate(
           str.maketrans('+/', '-_')
       )
   ```
2. **API Endpoint:** The URL format is `http://www.plantuml.com/plantuml/png/~1{encoded}`.
3. **Bypassing 403 Forbidden:** The PlantUML API blocks default Python `urllib` user agents. **You must set a `User-Agent` header** (e.g., `Mozilla/5.0`) when making the request.
   ```python
   import urllib.request
   
   url = f"http://www.plantuml.com/plantuml/png/~1{encoded}"
   req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
   with urllib.request.urlopen(req) as response:
       with open('diagram.png', 'wb') as f:
           f.write(response.read())
   ```
