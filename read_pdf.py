import pdfplumber
import sys
import os

def extract_pdf_text(pdf_path):
    full_text = ""
    with pdfplumber.open(pdf_path) as pdf:
        for page in pdf.pages:
            page_text = page.extract_text()
            if page_text:
                full_text += page_text + "\n"
    return full_text

if __name__ == "__main__":
    pdf_path = "Proposal_UTS_SmartPelayanan.pdf"
    
    if not os.path.exists(pdf_path):
        print(f"Error: File {pdf_path} tidak ditemukan!")
        sys.exit(1)
    
    print(f"Membaca {pdf_path}...")
    extracted_text = extract_pdf_text(pdf_path)
    
    output_path = "proposal_text.txt"
    with open(output_path, "w", encoding="utf-8") as f:
        f.write(extracted_text)
    
    print(f"Text extracted to {output_path}")
    print(f"Total karakter: {len(extracted_text)}")
    print("\n=== Preview (first 3000 chars) ===")
    print(extracted_text[:3000])
