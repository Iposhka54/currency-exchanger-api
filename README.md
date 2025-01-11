# Currency Exchange REST API Project

![first_photo.png](https://github.com/VladislavLevchikIsAProger/currency_exchange/assets/153897612/c09228bc-fb39-4492-a9e4-22a73c83532e)

# Overview

REST API for describing currencies and exchange rates.
Allows you to view and edit lists of currencies and exchange rates, and calculate conversions of any amount from one
currency to another.The idea for the project was taken from [here](https://zhukovsd.github.io/java-backend-learning-course/Projects/CurrencyExchange/)

## Technologies / tools used:

- JDBC
- SQL
- Jakarta Servlets
- SQLite
- Postman
- Maven

## Database diagram

![db-diagram.png](https://github.com/VladislavLevchikIsAProger/currency_exchange/assets/153897612/a5f410d4-8c37-43da-9c79-91e5f90a48bb)

## API features

### Currencies

#### GET `/currencies`

Returns list of all currencies. Example of response:

```json
[
  {
    "id": 0,
    "name": "United States dollar",
    "code": "USD",
    "sign": "$"
  },
  {
    "id": 1,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
  },
  "..."
]
```

#### GET `/currency/USD`

Returns particular currency. The currency code is specified in the query address Example of response:

```json
[
  {
    "id": 0,
    "name": "United States dollar",
    "code": "USD",
    "sign": "$"
  }
]
```

#### POST `/currencies`

Adding a new currency to the database. Data is passed in the body of request in the x-www-form-urlencoded. The form
fields are `name`, `code`, `symbol`. Example of response (inserted record):

```json
[
  {
    "id": 2,
    "name": "Czech Koruna",
    "code": "CZK",
    "sign": "Kč"
  }
]
```

### Exchange rates

#### GET `/exchangeRates`

Returns list of all exchange rates. Example of response:

```json
[
  {
    "id": 0,
    "baseCurrency": {
      "id": 0,
      "name": "United States dollar",
      "code": "USD",
      "sign": "$"
    },
    "targetCurrency": {
      "id": 1,
      "name": "Euro",
      "code": "EUR",
      "sign": "€"
    },
    "rate": 0.93
  },
  {
    "id": 1,
    "baseCurrency": {
      "id": 0,
      "name": "United States dollar",
      "code": "USD",
      "sign": "$"
    },
    "targetCurrency": {
      "id": 2,
      "name": "Czech Koruna",
      "code": "CZK",
      "sign": "Kč"
    },
    "rate": 22.16
  },
  "..."
]
```

#### POST `/exchangeRates`

Adding a new exchange rate to the database. Data is passed in the body of request in the x-www-form-urlencoded. The form
fields are `baseCurrencyCode`, `targetCurrencyCode`, `rate`. Example of response (inserted record):

```json
[
  {
    "id": 2,
    "baseCurrency": {
      "id": 1,
      "name": "Euro",
      "code": "EUR",
      "sign": "€"
    },
    "targetCurrency": {
      "id": 2,
      "name": "Czech Koruna",
      "code": "CZK",
      "sign": "Kč"
    },
    "rate": 23.75
  }
]
```

#### GET `/exchangeRate/USDEUR`

Returns a particular exchange rate. The currency pair is specified by consecutive currency codes in the query address.
Example of response:

```json
[
  {
    "id": 0,
    "baseCurrency": {
      "id": 0,
      "name": "United States dollar",
      "code": "USD",
      "sign": "$"
    },
    "targetCurrency": {
      "id": 1,
      "name": "Euro",
      "code": "EUR",
      "sign": "€"
    },
    "rate": 0.93
  }
]
```

#### PATCH `/exchangeRate/USDEUR`

Updates the existing exchange rate in the database. The currency pair is specified by consecutive currency codes in the
query address. The data is passed in the body of the request in the x-www-form-urlencoded. The only form field
is `rate`.
Example of response (inserted record):

```json
[
  {
    "id": 1,
    "baseCurrency": {
      "id": 0,
      "name": "United States dollar",
      "code": "USD",
      "sign": "$"
    },
    "targetCurrency": {
      "id": 2,
      "name": "Czech Koruna",
      "code": "CZK",
      "sign": "Kč"
    },
    "rate": 22.24
  }
]
```

## Currency exchange

#### GET `/exchange?from=BASE_CURRENCY_CODE&to=TARGET_CURRENCY_CODE&amount=$AMOUNT`

Calculate the conversion of a particular amount of money from one currency to another. The currency pair and amount is
specified in the query address. Example of response:

```json
{
  "baseCurrency": {
    "id": 0,
    "name": "United States dollar",
    "code": "USD",
    "sign": "$"
  },
  "targetCurrency": {
    "id": 2,
    "name": "Czech Koruna",
    "code": "CZK",
    "sign": "Kč"
  },
  "rate": 22.24,
  "amount": 100.00,
  "convertedAmount": 2224.00
}
```
## Requirements
  + Java 17+
  + Apache Maven
  + Tomcat 10
  + Intellij IDEA

## Project launch

1. Clone the repository:
   ```
   git clone https://github.com/VladislavLevchikIsAProger/currency_exchange.git
   ```

2. Open Intellij IDEA and in Main Menu -> Open select the folder you have decloned.
   
3. In Intellij IDEA, select Run -> Edit Configuration.
  
4. In the pop-up window, click "+" and add Tomcat :
   
    ![Add tomcat](https://github.com/VladislavLevchikIsAProger/currency_exchange/assets/153897612/fcb0c610-73a6-4e1b-adf1-3751e638863f)

5. Then click "Fix" : 

    ![Fix botton](https://github.com/VladislavLevchikIsAProger/currency_exchange/assets/153897612/516b7afb-42ef-4374-b96e-2a49d3f866c9)

6. In the window that pops up, select :

   ![War exploded](https://github.com/VladislavLevchikIsAProger/currency_exchange/assets/153897612/db1dd9bc-43bf-41ea-9a3b-8adbada0d580)


7. In the Application context leave the following :
   
   ![Application context](https://github.com/VladislavLevchikIsAProger/currency_exchange/assets/153897612/895091c7-dd29-49b9-8edc-c9b5f29cf018)

8. Click Apply and start Tomcat

## Communication
My Telegram - https://t.me/IamNotARapperr
