---
name: word-document-improvement-2
description: Enhancing Word documents with automated diagram integration and role-specific content customization
source: auto-skill
extracted_at: '2026-06-07T04:55:26.749Z'
---

When tasked with improving a Word document (`.docx`) report for a software project—especially to align it with specific code changes (like removing a role) and adding visuals (diagrams, screenshots)—a combined scripting approach is highly effective.

### Approach to Document Updating

1. **Text Cleansing & Alignment:** 
   Use `python-docx` to iterate through all paragraphs and dynamically remove or replace outdated terms.
   ```python
   from docx import Document
   doc = Document('report.docx')
   for para in doc.paragraphs:
       if 'superadmin' in para.text.lower():
           para.text = para.text.replace('Superadmin', '').replace('superadmin', '')
       if '3 Role' in para.text:
           para.text = para.text.replace('3 Role', '2 Role')
   ```

2. **Diagram Generation:** 
   Generate necessary architecture diagrams (ERD, Class, API, Flowcharts) via PlantUML HTTP API using zlib and base64 encoding (see `plantuml-api-diagram-generation` skill). Note that if encountering 403 Forbidden, setting `User-Agent: Mozilla/5.0` is required. If a generated image appears to be 0 bytes or corrupted when generated locally via API, verify the PlantUML syntax and URL length.

3. **Image & Screenshot Injection:** 
   Append the newly generated diagrams and existing local project screenshots to the document. Use headers to clearly label each visual.
   ```python
   from docx.shared import Inches
   import os

   doc.add_heading('Lampiran: Diagram dan Screenshot Aplikasi', level=1)
   
   if os.path.exists('image2.jpg'):
       doc.add_heading('Entity Relationship Diagram (ERD)', level=2)
       doc.add_picture('image2.jpg', width=Inches(6.0))

   screenshots = [('login.png', 'Halaman Login')]
   for filename, desc in screenshots:
       if os.path.exists(filename):
           doc.add_heading(desc, level=2)
           doc.add_picture(filename, width=Inches(5.5))
   
   doc.save('report.docx')
   ```

4. **Iterative Verification:**
   After injecting, verify the file size of the resulting `.docx` to ensure the images were successfully embedded. This ensures that the automated improvements (code alignment and visual enrichment) are accurately reflected in the final deliverable.