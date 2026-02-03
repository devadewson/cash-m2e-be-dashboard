create or replace PROCEDURE TRADE_ACCOUNT_STATS (
    userId IN VARCHAR2,
    corpId IN VARCHAR2,
    tradeCategory IN VARCHAR2,
    acctType IN VARCHAR2,
    CURSOR OUT SYS_REFCURSOR
) AS
    totalOutstandingBal NUMBER := 0;
    noOfAccounts NUMBER := 0;
    baseCcy VARCHAR2(3) := 'MYR';
    convertedOutstandingBal NUMBER := 0;
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
    FOR rec IN (
        SELECT
            tad.currency AS fromCcy,
            tad.outstanding AS outstandingBal
        FROM
            tbl_portfolio_trd tad
        INNER JOIN
            MV_USER_ENTITLEMENT mv
            ON mv.cif_no = tad.cif_no
        WHERE
            mv.corp_usr_id = userId
            AND tad.category = tradeCategory
            AND mv.product = 'TRD'
            AND mv.corp_id = corpId
    ) LOOP
        IF rec.fromCcy = baseCcy THEN
            convertedOutstandingBal := rec.outstandingBal;
        ELSE
            BEGIN
                -- Retrieve conversion rate from the pre-fetched collection
                IF conversionRates.EXISTS(rec.fromCcy) THEN
                    convertedOutstandingBal := (rec.outstandingBal * conversionRates(rec.fromCcy).reval_rate) / conversionRates(rec.fromCcy).conversion_unit;
                ELSE
                    -- Set error message and raise exception if conversion rate is missing
                    missingConversionRateMessage := 'Missing conversion rate for currency: ' || rec.fromCcy;
                    RAISE_APPLICATION_ERROR(-20001, missingConversionRateMessage);
                END IF;
            END;
        END IF;

        -- Accumulating totals
        totalOutstandingBal := totalOutstandingBal + convertedOutstandingBal;
        noOfAccounts := noOfAccounts + 1;
    END LOOP;

    -- Open the cursor for returning the result
    OPEN CURSOR FOR
    SELECT
        ROUND(totalOutstandingBal, 2) AS totalOutstandingBal,
        noOfAccounts AS noOfAccounts
    FROM
        DUAL;
END TRADE_ACCOUNT_STATS;