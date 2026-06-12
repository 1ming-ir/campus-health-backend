$ErrorActionPreference = "Stop"
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
$patterns = @("*.java","*.yml","*.yaml","*.sql","*.json","*.xml","*.md","Dockerfile","Procfile")
foreach ($pattern in $patterns) {
  Get-ChildItem -Path $PSScriptRoot\.. -Recurse -File -Filter $pattern | Where-Object { $_.FullName -notmatch "\\target\\|\\.git\\" } | ForEach-Object {
    $text = [System.IO.File]::ReadAllText($_.FullName)
    $text = $text -replace [char]0xFEFF, ''
    [System.IO.File]::WriteAllText($_.FullName, $text, $utf8NoBom)
  }
}
Write-Host "encoding normalized"