create or replace FUNCTION GET_BASE_CURRENCY (
    corpId IN VARCHAR2
) RETURN VARCHAR AS
    baseCurrency VARCHAR2(3);
BEGIN

    BEGIN
        SELECT mc.CD INTO baseCurrency
        FROM TBL_MT_CURRENCY@cts_sso mc
        LEFT JOIN TBL_MT_COUNTRY@cts_sso ct ON mc.MT_COUNTRY_ID = ct.Id
        INNER JOIN TBL_CORP@cts_sso c ON ct.CD = c.SYS_PVD_CD
        WHERE c.CORP_ID = corpId
        AND mc.IS_DEL = 'N' AND ct.IS_DEL = 'N' AND c.IS_DEL = 'N'
        AND mc.IS_DEACTIVATED = 'N' AND mc.IS_DEACTIVATED = 'N' AND c.IS_DEACTIVATED = 'N';
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20002, 'No base currency found for the given corp ID.');
    END;

    RETURN baseCurrency;
END GET_BASE_CURRENCY;