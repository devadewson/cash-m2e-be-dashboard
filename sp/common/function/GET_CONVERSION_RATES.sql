create or replace FUNCTION GET_CONVERSION_RATES (
    corpId IN VARCHAR2
) RETURN ConversionRateTable AS
    conversionRates ConversionRateTable := ConversionRateTable();
    provider VARCHAR2(10);
BEGIN
    -- Fetch the provider code for the corporation user
    BEGIN
        SELECT SYS_PVD_CD
        INTO provider
        FROM TBL_CORP@cts_sso
        WHERE CORP_ID = corpId
        AND IS_DEL = 'N' AND IS_DEACTIVATED = 'N';
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20003, 'No corporation user found for the given corp ID.');
    END;

    -- Fetch all necessary conversion rates and populate the nested table
    FOR rec IN (
        SELECT currency, reval_rate, conversion_unit
        FROM tbl_mt_foreign_exchange@cts_sso
        WHERE provider = provider
        AND IS_DEL = 'N' AND IS_DEACTIVATED = 'N'
    ) LOOP
        conversionRates.EXTEND;  -- Extend the collection
        conversionRates(conversionRates.COUNT) := ConversionRateRec(
            rec.currency,
            rec.reval_rate,
            rec.conversion_unit
        );
    END LOOP;

    -- Return the populated conversion rates table
    RETURN conversionRates;
END GET_CONVERSION_RATES;