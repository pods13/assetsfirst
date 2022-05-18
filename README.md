# Assets

## Usage
```
docker-compose -f db.dev.yaml up -d --build
docker-compose -f db.dev.yaml down 
```

## Deployment
Use command below to make sure that database will have correct character set and collation:

``ALTER DATABASE dbname CHARACTER SET utf8 COLLATE utf8_general_ci;``
