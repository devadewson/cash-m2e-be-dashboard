create or replace PROCEDURE DEPOSIT_CA_PAYMENT_HIST_LIST (
    acctNumber      IN VARCHAR2,
    paymentType     IN VARCHAR2 DEFAULT NULL,
    trxType         IN VARCHAR2 DEFAULT NULL,
    startDate       IN VARCHAR2 DEFAULT NULL,
    endDate         IN VARCHAR2 DEFAULT NULL,
    userId          IN VARCHAR2,
    corpId          IN VARCHAR2,
    pageNumber      IN NUMBER,
    pageSize        IN NUMBER,
    CURSOR OUT SYS_REFCURSOR
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
        SELECT * FROM (
            SELECT COUNT(*) OVER() as totalCount,
                pd.VALUE_DATE AS "date",
                pd.PAYER_REFERENCE_NO AS bankRefNo,
                CASE
                    WHEN pd.INCOMING_OUTGOING_FLAG = 'I' THEN 'Incoming'
                    WHEN pd.INCOMING_OUTGOING_FLAG = 'O' THEN 'Outgoing'
                    ELSE NULL
                END AS trxType,
                pd.AMOUNT_CREDITED AS trxAmount,
                CASE
                    WHEN pd.TRANSACTION_TYPE = 'D' THEN pd.AMOUNT_CREDITED ELSE NULL END AS debit,
                CASE
                    WHEN pd.TRANSACTION_TYPE = 'C' THEN pd.AMOUNT_CREDITED ELSE NULL END AS credit,
                pd.UETR_NO AS uetrNumber,
                pd.CURRENCY AS currency,
                ROW_NUMBER() OVER (ORDER BY pd.ACCOUNT_NO) AS row_num
            FROM
                tbl_corp@cts_sso c
                INNER JOIN tbl_corp_usr_group@cts_sso cug ON c.id = cug.corp_id
                INNER JOIN tbl_corp_usr_group_usr@cts_sso cugu ON cug.id = cugu.corp_usr_group_id
                INNER JOIN tbl_corp_usr_group_acct@cts_sso cuga ON cugu.corp_usr_group_id = cuga.corp_usr_group_id
                INNER JOIN tbl_corp_accts@cts_sso ca ON cuga.corp_acct_id = ca.id
                INNER JOIN tbl_acct@cts_sso a ON ca.acct_id = a.id
                INNER JOIN TBL_PAYMENT_DETAIL pd ON a.ACCT_NO = pd.ACCOUNT_NO
            WHERE
                cugu.corp_usr_id = userId AND c.corp_id = corpId
                AND pd.ACCOUNT_NO = acctNumber
        )
        WHERE row_num BETWEEN v_start_index AND v_end_index;
END DEPOSIT_CA_PAYMENT_HIST_LIST;