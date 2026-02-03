create or replace FUNCTION CONVERT_CURRENCY (
    baseCcy         IN VARCHAR2,
    fromCurrency    IN VARCHAR2,
    amount          IN NUMBER,
    conversionRates IN ConversionRateTable
) RETURN NUMBER IS
    conversionRate NUMBER;
    missingConversionRateMessage VARCHAR2(4000);
BEGIN

    IF fromCurrency = baseCcy THEN
        RETURN amount;
    ELSE
        BEGIN
            -- Loop through the conversion rates to find the correct one
            FOR i IN 1 .. conversionRates.COUNT LOOP
                IF conversionRates(i).currency = fromCurrency THEN
                    conversionRate := conversionRates(i).reval_rate / conversionRates(i).conversion_unit;
                    RETURN amount * conversionRate;
                END IF;
            END LOOP;
        END;
    END IF;

    missingConversionRateMessage := 'Missing conversion rate for currency: ' || fromCurrency;
    RAISE_APPLICATION_ERROR(-20001, missingConversionRateMessage);
END CONVERT_CURRENCY;