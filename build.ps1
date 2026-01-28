param(
    [string]$ImageName = "my-spring-app",
    [string]$Tag = "latest"
)

mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) { exit 1 }

docker build -t "${ImageName}:${Tag}" .