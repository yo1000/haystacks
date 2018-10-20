# haystacks
"haystacks" is Web based Database schema viewer.


## Usage


### Required options
```bash
curl -L -o haystacks-assembly-mysql-latest.jar \
  https://github.com/yo1000/haystacks/releases/download/v0.0.3/haystacks-assembly-mysql-0.0.3.jar

java -jar haystacks-assembly-mysql-latest.jar \
  --spring.datasource.url=jdbc:mysql://<host>:<port>/<database> \
  --spring.datasource.name=<schema> \
  --spring.datasource.username=<username> \
  --spring.datasource.password=<password>
```


### All options
```bash
curl -L -o haystacks-assembly-mysql-latest.jar \
  https://github.com/yo1000/haystacks/releases/download/v0.0.3/haystacks-assembly-mysql-0.0.3.jar

java -jar haystacks-assembly-mysql-latest.jar \
  --spring.datasource.url=jdbc:mysql://<Host>:<Port>/<Database> \
  --spring.datasource.name=<Schema> \
  --spring.datasource.username=<Username> \
  --spring.datasource.password=<Password> \
  --haystacks.note.file.store-location=<Notes Save file location> \
  --haystacks.note.file.create-on-missing=<File creation on missing: default(true)> \
  --haystacks.web.ssr=<Rendering mode: default(true)> \
  --haystacks.web.title=<Site title> \
  --haystacks.web.logo=<Header logo url by svg> \
  --haystacks.web.favicon=<Favicon url>
```


## Quick Start


### Launch binary
with external Database. (MySQL [sakila database](https://dev.mysql.com/doc/sakila/en/) by [Docker container](https://hub.docker.com/r/thebinarypenguin/mysql-sakila/))

```bash
docker run -d \
  --name mysql-employees \
  -p 3306:3306 \
  thebinarypenguin/mysql-sakila:latest

curl -L -o haystacks-assembly-mysql-latest.jar \
  https://github.com/yo1000/haystacks/releases/download/v0.0.3/haystacks-assembly-mysql-0.0.3.jar

java -jar haystacks-assembly-mysql-latest.jar \
  --spring.datasource.url=jdbc:mysql://localhost:3306/sakila \
  --spring.datasource.name=sakila \
  --spring.datasource.username=root \
  --spring.datasource.password=sakila
```

Browse to [http://localhost:8080](http://localhost:8080)


### Launch from source
with embedded DEMO Database.

```bash
git clone https://github.com/yo1000/haystacks.git
cd haystacks

./mvnw clean install
./mvnw clean spring-boot:run \
  -pl haystacks-assemblies/haystacks-assembly-mysql \
  -P demo
```

Browse to [http://localhost:8080](http://localhost:8080)


## Screenshots
![Screenshot1](docs/screenshots/haystacks-screenshot1.png?raw=true)

![Screenshot2](docs/screenshots/haystacks-screenshot2.png?raw=true)

![Screenshot3](docs/screenshots/haystacks-screenshot3.png?raw=true)

![Screenshot4](docs/screenshots/haystacks-screenshot4.png?raw=true)
