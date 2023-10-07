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
- [x] Redesign home page
- [x] Setup schedule job that recalculate position p&l and keep results in a separate db table, use these data in positionView
- [x] On balance and contributions plots on yAxis use short number form
- [x] Migrate to spring boot 3
- [x] Allow user to change name of tag category
</details>

<details>
  <summary>1.2.0 Features</summary>

- [x] Allocation card - custom type to group by tags
- [x] Switch to flyway is impossible due to usage of mysql 5.7(too old)
- [x] Introduce Portfolio currency, as a column in db table
- [x] Show total contributions this year
- [x] Add usd exchange provider to support usd currency for the whole portfolio
- [x] Dividend income card - sum up payments from the same ticker
- [ ] Tax widget
- [ ] Company Page - ownership structure
- [ ] Migrate Angular from v14 to v16
- [ ] Switch from @swimlane/ngx-datatable to mat-table
- [ ] Keep showing accumulated dividends for closed positions
- [ ] Provide functionality to change a username
- [ ] Show formatted price in sectoral distribution widget
- [ ] Show percentage in sectoral distribution widget
- [ ] Allow a user to change their portfolio currency
- [ ] Add includeForecastedDividends option as edit option of Dividend income card 
- [ ] On Contribution card show total contribution vs previous year contributions 
- [ ] Ability to select specific timelapse(year) and render all cards with info only about it
- [ ] Balance card - keep precalculated portfolio value in db in order to show more points on a plot
- [ ] Balance card - show portfolio current value on a plot as well 
- [ ] New position widget
- [ ] New trades widget
- [ ] Ability to group positions
- [ ] On home page show demo portfolio values
- [ ] Asset allocation widget option to show invested money instead of current evaluation
- [ ] Rework the positions table - use mat-table(should support at least ordering by % of portfolio column)
</details>

## Redeploy flow
```
docker-compose -f docker-compose.prod.yaml pull
docker-compose -f docker-compose.prod.yaml up -d --build
docker image prune -a
docker-compose -f docker-compose.prod.yaml down
```
