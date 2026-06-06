[xml]$xml = Get-Content 'C:\Users\MyBook Hype AMD\Downloads\prakoopp\docx_extracted\word\document.xml'
$ns = New-Object System.Xml.XmlNamespaceManager($xml.NameTable)
$ns.AddNamespace('w', 'http://schemas.openxmlformats.org/wordprocessingml/2006/main')
$paragraphs = $xml.SelectNodes('//w:p', $ns)
foreach ($p in $paragraphs) {
    $texts = $p.SelectNodes('.//w:t', $ns)
    $line = ''
    foreach ($t in $texts) {
        $line += $t.InnerText
    }
    if ($line) {
        Write-Output $line
    }
}
