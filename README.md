# Assets
[SwaggerUI](http://localhost:8080/swagger-ui/index.html)
[OpenAPI](http://localhost:8080/v3/api-docs)

## Usage
```
docker-compose -f db.dev.yaml up -d --build
docker-compose -f db.dev.yaml down 
```
## App 1.0.0 Features
- [ ] Create an account and login with it 
- [x] Use dashboard page to setup widgets and observe portfolio data
- [X] 4 main widgets are available - ALLOCATION, DIVIDEND_INCOME, SECTORAL_DISTRIBUTION, and BALANCE
- [x] Portfolio trades can be managed on a separate page 'Trades'
- [x] By opening 'Holdings' page, portfolio holdings can be looked on
- [x] BALANCE widget depicts portfolio balance, which is calculated based on the securities recent price(~15m delay)
