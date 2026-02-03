create or replace PROCEDURE DEPOSIT_CA_TRX_HIST_LIST (
    pageNumber      IN NUMBER,
    pageSize        IN NUMBER,
    acctNumber      IN VARCHAR2,
    startDate       IN VARCHAR2 DEFAULT NULL,
    startTime       IN VARCHAR2 DEFAULT NULL,
    endDate         IN VARCHAR2 DEFAULT NULL,
    endTime         IN VARCHAR2 DEFAULT NULL,
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
				pad.ACCOUNT_NO AS accountNo,
				pad.TRANSACTION_DATE AS "date",
				pad.TXN_REF AS trxReference,
				pad.TXN_DESCRIPTION AS trxDesc,
				CASE
					WHEN pad.TXN_TYPE = 'D' THEN pad.TXN_AMOUNT ELSE NULL END AS debit,
				CASE
					WHEN pad.TXN_TYPE = 'C' THEN pad.TXN_AMOUNT ELSE NULL END AS credit,
				pad.PROC_TIME AS processingTime,
                pad.TRX_DESCRIPTION_2 AS trxDesc2,
                pad.TRX_DESCRIPTION_3 AS trxDesc3,
                pad.TRX_DESCRIPTION_4 AS trxDesc4,
                pad.TXN_DESCRIPTION_5 AS trxDesc5,
                pad.TXN_DESCRIPTION_6 AS trxDesc6,
                pad.CURRENCY_CODE AS currency,
                pad.SOURCE_CODE AS sourceCode,
                pad.TELLER_ID AS tellerId,
                pad.OWNER_INSTITUTION AS branch,
                pad.TRANSACTION_CODE AS trxCode,
                pad.AUXILIARY_TXN_CODE AS auxTrxCode,
                pad.EXPIRY_DATE AS expiryDate,
                pad.BEGIN_BALANCE AS beginBalance,
                pad.END_BALANCE AS endBalance,
                pad.VA_ACCOUNT_NO AS virtualAcct,
				ROW_NUMBER() OVER (ORDER BY pad.ACCOUNT_NO) AS row_num
			FROM
                TBL_PORTFOLIO_ACTIVITY_DP pad
                INNER JOIN MV_USER_ENTITLEMENT mv
                ON mv.acct_no = pad.ACCOUNT_NO
            WHERE
                mv.corp_usr_id = userId AND mv.corp_id = corpId
                AND pad.ACCOUNT_NO = acctNumber
            )
		WHERE row_num BETWEEN v_start_index AND v_end_index;
END DEPOSIT_CA_TRX_HIST_LIST;