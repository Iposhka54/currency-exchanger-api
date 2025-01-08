CREATE TABLE ExchangeRates(
                              id INTEGER PRIMARY KEY AUTOINCREMENT,
                              base_currency_id INTEGER NOT NULL,
                              target_currency_id INTEGER NOT NULL,
                              rate DECIMAL(18, 6) NOT NULL,
                              CONSTRAINT base_target UNIQUE (base_currency_id, target_currency_id),
                              FOREIGN KEY(base_currency_id) REFERENCES Currencies(id),
                              FOREIGN KEY(target_currency_id) REFERENCES Currencies(id)
);