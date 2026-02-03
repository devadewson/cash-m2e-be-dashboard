create or replace PROCEDURE DEPOSIT_CA_ACCOUNT_STATS (
    userId IN VARCHAR2,
    corpId IN VARCHAR2,
    CURSOR OUT SYS_REFCURSOR
) AS
    totalLedgerBal NUMBER:= 0;
    totalAvailableBal NUMBER := 0;
    noOfAccounts NUMBER := 0;
    ledgerBal NUMBER := 0;
    availableBal NUMBER := 0;
    baseCcy VARCHAR2(3) := 'MYR';
    revalRate NUMBER;
    conversionUnit NUMBER;
    convertedLedgerBal NUMBER := 0;
    convertedAvailableBal NUMBER := 0;
    corpUserProvider VARCHAR2(20);

    -- Record type for conversion rates
    TYPE ConversionRateRec IS RECORD (
        reval_rate NUMBER,
        conversion_unit NUMBER
    );

    -- Table type for conversion rates
    TYPE ConversionRateTable IS TABLE OF ConversionRateRec INDEX BY VARCHAR2(3);

    -- Instance of conversion rates table
    conversionRates ConversionRateTable;

    -- Custom exception for missing conversion rate
    missingConversionRateException EXCEPTION;

    -- Error message
    missingConversionRateMessage VARCHAR2(255);

BEGIN
    SELECT sys_pvd_cd into corpUserProvider FROM tbl_corp_usr@CTS_SSO where usr_id = userId;
     -- Fetch all necessary conversion rates in a single query
    FOR rec IN (
        SELECT currency, reval_rate, conversion_unit
        FROM tbl_mt_foreign_exchange@cts_sso
        WHERE provider = corpUserProvider
    ) LOOP
        conversionRates(rec.currency).reval_rate := rec.reval_rate;
        conversionRates(rec.currency).conversion_unit := rec.conversion_unit;
    END LOOP;

    -- Loop through deposit accounts and calculate totals
    FOR rec in (SELECT
            pdp.currency_code as fromCcy,
            pdp.ledger_balance as ledgerBal,
            pdp.cust_available_bal as availableBal
        FROM
            tbl_portfolio_dp pdp
            INNER JOIN
            MV_USER_ENTITLEMENT mv
            ON mv.acct_no = pdp.account_no
        WHERE
            mv.corp_usr_id = userId and mv.corp_id = corpId and mv.sub_product = 'CA' and mv.product = 'DEPOSIT')
    LOOP
            IF rec.fromCcy = baseCcy THEN
                convertedLedgerBal := rec.ledgerBal;
                convertedAvailableBal := rec.availableBal;
            ELSE
            BEGIN
                -- Retrieve conversion rate from the pre-fetched collection
                IF conversionRates.EXISTS(rec.fromCcy) THEN
                    convertedLedgerBal := (rec.ledgerBal * conversionRates(rec.fromCcy).reval_rate) / conversionRates(rec.fromCcy).conversion_unit;
                    convertedAvailableBal := (rec.availableBal * conversionRates(rec.fromCcy).reval_rate) / conversionRates(rec.fromCcy).conversion_unit;
                ELSE
                    -- Set error message and raise exception if conversion rate is missing
                    missingConversionRateMessage := 'Missing conversion rate for currency: ' || rec.fromCcy;
                    RAISE_APPLICATION_ERROR(-20001, missingConversionRateMessage);
                END IF;
            END;
            END IF;
            -- Accumulating totals
            totalLedgerBal := totalLedgerBal + convertedLedgerBal;
            totalAvailableBal := totalAvailableBal + convertedAvailableBal;
            noOfAccounts := noOfAccounts + 1;
    END LOOP;

    OPEN CURSOR FOR
    SELECT ROUND(totalLedgerBal, 2) as totalLedgerBal, ROUND(totalAvailableBal, 2) as totalAvailableBal, noOfAccounts as noOfAccounts FROM DUAL;
END DEPOSIT_CA_ACCOUNT_STATS;