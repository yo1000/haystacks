# haystacks
"haystacks" is Web based Database schema viewer.


## Quick Start
```bash
git clone https://github.com/yo1000/haystacks.git
cd haystacks

./mvnw clean install
./mvnw clean spring-boot:run \
  -pl haystacks-assemblies/haystacks-assembly-mysql \
  -Dspring.datasource.url=jdbc:mysql://<host>:<port>/<database> \
  -Dspring.datasource.name=<schema> \
  -Dspring.datasource.username=<username> \
  -Dspring.datasource.password=<password>
```

## Try DEMO
```bash
git clone https://github.com/yo1000/haystacks.git
cd haystacks

./mvnw clean install
./mvnw clean spring-boot:run -pl haystacks-assemblies/haystacks-assembly-mysql -P demo
```

## Screenshots
![Screenshot1](docs/screenshots/haystacks-screenshot1.png?raw=true)

![Screenshot2](docs/screenshots/haystacks-screenshot2.png?raw=true)

![Screenshot3](docs/screenshots/haystacks-screenshot3.png?raw=true)

![Screenshot4](docs/screenshots/haystacks-screenshot4.png?raw=true)
