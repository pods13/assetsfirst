# AssetsFirst
[SwaggerUI](http://localhost:8080/swagger-ui/index.html)
[OpenAPI](http://localhost:8080/v3/api-docs)

## Usage
```
docker-compose up -d --build
docker-compose down 
```
<details>
  <summary>1.0.0 Features</summary>

- [x] Create an account and login with it 
- [x] Use dashboard page to setup widgets and observe portfolio data
- [X] 4 main widgets are available - ALLOCATION, DIVIDEND_INCOME, SECTORAL_DISTRIBUTION, and BALANCE
- [x] Portfolio trades can be managed on a separate page 'Trades'
- [x] By opening 'Holdings' page, portfolio holdings can be looked on
- [x] BALANCE widget depicts portfolio balance, which is calculated based on the securities recent price(~15m delay)
</details>

<details>
  <summary>1.1.0 Features</summary>

- [x] Show identifier with company name on position page(check out tradingview for inspiration)
- [x] Fill up Upcoming dividend date column with relevant data
- [x] In Sectoral distribution use current value of positions instead of initial one
- [x] Company Page - show company name
- [x] Company Page - ownership structure
- [x] Redesign home page
- [x] Setup schedule job that recalculate position p&l and keep results in a separate db table, use these data in positionView
- [ ] Allow user to change name of tag category
- [ ] Provide functionality to change a username
- [ ] Show percentage in sectoral distribution widget
- [ ] Introduce Portfolio currency, as a column in db table 
- [ ] Allow a user to change their portfolio currency
</details>

## Redeploy flow
```
docker-compose -f docker-compose.prod.yaml pull
docker-compose -f docker-compose.prod.yaml up -d --build
docker-compose -f docker-compose.prod.yaml down
```
