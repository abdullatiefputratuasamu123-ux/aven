---
name: python-docx-automation
description: Automating Word document (DOCX) modifications using python-docx
source: auto-skill
extracted_at: '2026-06-07T04:36:29.740Z'
---

When a task requires modifying an existing `.docx` file (e.g., removing text, appending images), use the `python-docx` library via a Python script.

### Key Learnings
1. **Installation:** `pip install python-docx`
2. **Loading & Fallback:** Use `os.path.exists()` to check if the file exists. If it does, load with `doc = Document('file.docx')`. Otherwise, create a new one with `doc = Document()`.
3. **Text Replacement:** Iterate through `doc.paragraphs` and replace text inside `para.text`. Example:
   ```python
   from docx import Document
   
   doc = Document('file.docx')
   for para in doc.paragraphs:
       if 'old_text' in para.text.lower():
           para.text = para.text.replace('old_text', 'new_text')
   ```
4. **Appending Images:** Use `doc.add_picture('image.jpg', width=Inches(6.0))` (requires `from docx.shared import Inches`).
5. **Saving:** Always save the document back using `doc.save('file.docx')`.
