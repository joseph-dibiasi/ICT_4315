# This script launches multiple concurrent clients to test the parking system server.
# It uses PowerShell jobs to run clients in parallel and collects their outputs.
# If no client count is specified, it defaults to 20.

param(
    [int]$clients = 20
)

function Get-ClientClasspath {
    param([string]$projectRoot)

    $target = Join-Path $projectRoot 'target'
    $shadedJar = Join-Path $target 'parking-system-0.0.1-SNAPSHOT.jar'
    $classes = Join-Path $target 'classes'

    if (Test-Path $shadedJar) {
        return $shadedJar
    }

    if (Test-Path $classes) {
        return $classes
    }

    throw "Could not locate compiled classes or the packaged jar. Run 'mvn package' first."
}

$projectRoot = $PSScriptRoot
$classpath = Get-ClientClasspath -projectRoot $projectRoot

Write-Host "Launching $clients concurrent clients against localhost:7777"
Write-Host "Using classpath: $classpath"

if ($clients -lt 0) {
    throw "Client count must be zero or greater."
}

$overallTimer = [System.Diagnostics.Stopwatch]::StartNew()
$jobs = @()
for ($i = 0; $i -lt $clients; $i++) {
    $jobs += Start-Job -ArgumentList $classpath, $i -ScriptBlock {
        param($clientClasspath, $clientIndex)

        try {
            $overallStart = [System.Diagnostics.Stopwatch]::StartNew()

            $customerName = "Test$clientIndex"
            $phoneNumber = "3030000$clientIndex"
            $license = "LIC$clientIndex"

            $customerTimer = [System.Diagnostics.Stopwatch]::StartNew()
            $customerCommand = "java -cp `"$clientClasspath`" clients.ServerClient CUSTOMER name=$customerName address=addr phonenumber=$phoneNumber 2>&1"
            $customerOutput = & cmd /c $customerCommand | Out-String
            $customerTimer.Stop()
            $customerMatch = [regex]::Match($customerOutput, 'CustomerID:\s*([0-9a-fA-F-]{36})')
            if (-not $customerMatch.Success) {
                throw "Could not extract customerId from customer response.`n$customerOutput"
            }

            $customerId = $customerMatch.Groups[1].Value
            $carTimer = [System.Diagnostics.Stopwatch]::StartNew()
            $carCommand = "java -cp `"$clientClasspath`" clients.ServerClient CAR ownerid=$customerId license=$license cartype=SUV 2>&1"
            $carOutput = & cmd /c $carCommand | Out-String
            $carTimer.Stop()
            $overallStart.Stop()

            [pscustomobject]@{
                Index = $clientIndex
                Success = $true
                CustomerId = $customerId
                CustomerOutput = $customerOutput.Trim()
                CarOutput = $carOutput.Trim()
                CustomerMs = [int]$customerTimer.ElapsedMilliseconds
                CarMs = [int]$carTimer.ElapsedMilliseconds
                TotalMs = [int]$overallStart.ElapsedMilliseconds
                Error = $null
            }
        } catch {
            [pscustomobject]@{
                Index = $clientIndex
                Success = $false
                CustomerId = $null
                CustomerOutput = $null
                CarOutput = $null
                CustomerMs = $null
                CarMs = $null
                TotalMs = $null
                Error = $_.Exception.Message
            }
        }
    }
}

Write-Host "Waiting for clients to finish..."
$summary = [ordered]@{
    Total = 0
    Succeeded = 0
    Failed = 0
    TotalMs = 0
}
if ($jobs.Count -gt 0) {
    $results = $jobs | Receive-Job -Wait -AutoRemoveJob
    foreach ($result in $results | Sort-Object Index) {
        $summary.Total++
        if ($result.Success) {
            $summary.Succeeded++
            $summary.TotalMs += $result.TotalMs
            Write-Host "--- Client $($result.Index) succeeded ---"
            Write-Host "Timing: customer=$($result.CustomerMs) ms, car=$($result.CarMs) ms, total=$($result.TotalMs) ms"
            Write-Host "--- Customer ---"
            Write-Host $result.CustomerOutput
            Write-Host "--- Car ---"
            Write-Host $result.CarOutput
        } else {
            $summary.Failed++
            Write-Host "--- Client $($result.Index) failed ---"
            Write-Host $result.Error
        }
    }
}

if ($summary.Succeeded -gt 0) {
    $average = [math]::Round($summary.TotalMs / $summary.Succeeded, 2)
    Write-Host "Average time per successful client: $average ms"
}
 $overallTimer.Stop()
Write-Host "Total elapsed time: $([math]::Round($overallTimer.Elapsed.TotalMilliseconds, 2)) ms"
Write-Host "Summary: $($summary.Succeeded)/$($summary.Total) succeeded, $($summary.Failed) failed."
Write-Host "All clients finished."
