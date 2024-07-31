Param (
    [Parameter(Mandatory = $true)][string]$env,
    [Parameter(Mandatory = $true)][string]$rg,
    [Parameter(Mandatory = $true)][string]$namespace,
    [string]$appIndex='01',
    [string]$redisImage='redis:7.0.11-alpine',
    [string]$redisUser='redis',
    [int]$redisReplicas=1,
    [int]$redisDiskSizeGb=10,
    [string]$redisMemoryRequest='500Mi',
    [string]$redisCpuRequest='300m',
    [string]$redisMemoryLimit='1000Mi',
    [string]$redisCpuLimit='600m',
    [string]$cronjobExpressionEvery5minutes='*/5 * * * *',
    [string]$cronjobExpressionEvery10minutes='*/10 * * * *'

)
#To be edited for multi instance : api key, bus frontal, bus backend, database... (see template.json)
$appNamespace=$namespace.Substring(3)
$appNamespace+="-$appIndex"

#To be dedicated tasks storage for the instance/appIndex
$storageCS = Get-StorageCString -rg $rg -storage "${namespace}tasks"

$secrets = Get-Secret -keyvault "${namespace}kv" -secretKeys SEARCH-BUS1, SEARCH-BUS2, MODULE-BUS-BACKEND, MODULE-BUS-FRONTAL, MODULE-API-KEY1, MODULE-API-KEY2, DB-APPLICATION-LOGIN, DB-APPLICATION-PWD,REDIS-SYS-PWD,REDIS-PWD -continueOnErrors $false

#dump to global context
$vars=@{
  BUS_CS_FRONT=$secrets['MODULE-BUS-FRONTAL']
  BUS_CS_BACKEND=$secrets['MODULE-BUS-BACKEND']
  API_KEY1=$secrets['MODULE-API-KEY1']
  API_KEY2=$secrets['MODULE-API-KEY2']
  app_namespace=$appNamespace
  STORAGE_CS=$storageCS
  DB_USER=$secrets['DB-APPLICATION-LOGIN']
  DB_PASSWORD=$secrets['DB-APPLICATION-PWD']
  DB_NAMESPACE="$namespace-openesldb-fog"
}
$vars.Keys | ForEach-Object { Write-Host "##vso[task.setvariable variable=$_;]$($vars.$_)" }