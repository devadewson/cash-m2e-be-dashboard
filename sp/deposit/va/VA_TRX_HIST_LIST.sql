create or replace NONEDITIONABLE PROCEDURE VA_TRX_HIST_LIST (
    pageNumber      IN NUMBER,
    pageSize        IN NUMBER,
    virtualAccNo    IN VARCHAR2,
    startDateTime   IN NVARCHAR2,
    endDateTime     IN NVARCHAR2,
    amtFrom         IN NUMBER DEFAULT NULL,
    amtTo           IN NUMBER DEFAULT NULL,
    trxCode         IN VARCHAR2 DEFAULT NULL,
    searchParam     IN VARCHAR2 DEFAULT NULL,
    userId          IN VARCHAR2,
    corpId          IN VARCHAR2,
    CURSOR          OUT SYS_REFCURSOR
) AS
    v_total_page    NUMBER;
    v_record_size   NUMBER;
    v_start_index   NUMBER;
    v_end_index     NUMBER;

BEGIN
    -- Calculate pagination index
    v_start_index := (pageNumber - 1) * pageSize + 1;
    v_end_index := pageNumber * pageSize;

    OPEN CURSOR FOR
        select * from (
            SELECT COUNT(*) OVER() as totalCount,
                pad.VA_ACCOUNT_NO AS virtualAccNo,
                TO_CHAR(pad.TXN_DATETIME, 'DD Mon YYYY HH24:MI') AS "date",
                pad.TXN_REF AS trxDescription,
                pad.TXN_DESCRIPTION AS trxDesc,
                CASE
                  WHEN pad.TXN_TYPE = 'D' THEN pad.TXN_AMOUNT ELSE NULL END AS debit,
                CASE
                  WHEN pad.TXN_TYPE = 'C' THEN pad.TXN_AMOUNT ELSE NULL END AS credit,
                pad.PROC_TIME AS processingTime,
                pad.CURRENCY_CODE AS currency,
                pad.SOURCE_CODE AS sourceCode,
                pad.TELLER_ID AS tellerId,
                pad.TRANSACTION_CODE AS trxCode,
                pad.AUXILIARY_TXN_CODE AS auxTrxCode,
                pad.EXPIRY_DATE AS expiryDate,
                pad.BEGIN_BALANCE AS beginBalance,
                pad.END_BALANCE AS endBalance,
                'REF' AS virtualAccRefNo,
                ROW_NUMBER() OVER (ORDER BY pad.ACCOUNT_NO) AS row_num
              FROM
                TBL_PORTFOLIO_ACTIVITY_DP pad
                INNER JOIN TBL_ACCT_VA@CTS_SSO ut on ut.VIRTUAL_ACCOUNT = pad.VA_ACCOUNT_NO
                INNER JOIN MV_USER_ENTITLEMENT mv on ut.PARENT_ACCOUNT = mv.ACCT_NO
            --    ON mv.acct_no = pad.ACCOUNT_NO
              WHERE
                mv.corp_usr_id = userId AND mv.corp_id = corpId
                AND pad.VA_ACCOUNT_NO = virtualAccNo
                AND (pad.TXN_DATETIME >= NVL(startDateTime, pad.TXN_DATETIME) AND pad.TXN_DATETIME <= NVL(endDateTime, pad.TXN_DATETIME))
                AND (pad.TXN_AMOUNT >= NVL(amtFrom, pad.TXN_AMOUNT) AND pad.TXN_AMOUNT <= NVL(amtTo, pad.TXN_AMOUNT))
                AND (trxCode IS NULL OR pad.TRANSACTION_CODE = trxCode)
                AND (UPPER(pad.TXN_DESCRIPTION) LIKE '%' || UPPER(searchParam) || '%' OR (NVL2(searchParam, pad.TXN_REF, pad.TXN_REF) LIKE NVL2(searchParam, '%'|| searchParam || '%', pad.TXN_REF)))
             )
		WHERE row_num BETWEEN v_start_index AND v_end_index;
END VA_TRX_HIST_LIST;