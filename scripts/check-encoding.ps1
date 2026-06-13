$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$patterns = @('?/p>','?/h1>','?/h2>','?/span>')
$files = Get-ChildItem -Path $root -Recurse -Include *.java,*.sql,*.yml,*.xml -File | Where-Object { $_.FullName -notmatch '\\target\\' }
$bad = @()
foreach ($file in $files) {
  $bytes = [System.IO.File]::ReadAllBytes($file.FullName)
  if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) { $bad += "BOM: $($file.FullName)" }
  $text = [System.Text.Encoding]::UTF8.GetString($bytes)
  foreach ($p in $patterns) { if ($text.Contains($p)) { $bad += "Broken fragment [$p]: $($file.FullName)" } }
}
if ($bad.Count -gt 0) { $bad | ForEach-Object { Write-Host $_ }; exit 1 }
Write-Host 'encoding-check ok'
