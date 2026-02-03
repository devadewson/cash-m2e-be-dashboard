create or replace PROCEDURE HIRE_PURCHASE_TRX_HISTORY_LISTING (
    acctNumber      IN NVARCHAR2,
    searchParam     IN NVARCHAR2,
    trxCode         IN NVARCHAR2,
    startDateTime   IN NVARCHAR2,
    endDateTime     IN NVARCHAR2,
    amtFrom         IN NVARCHAR2,
    amtTo           IN NVARCHAR2,
    userId          IN NVARCHAR2,
    corpId          IN NVARCHAR2,
    pageNumber      IN NUMBER,
    pageSize        IN NUMBER,
    CURSOR          OUT SYS_REFCURSOR
) AS
    v_start_index NUMBER;
    v_end_index   NUMBER;
BEGIN
    -- Calculate pagination index
    v_start_index := (pageNumber - 1) * pageSize + 1;
    v_end_index := pageNumber * pageSize;

    OPEN CURSOR FOR
        SELECT * FROM (
            SELECT /*+  index(pahp IDX_PORTFOLIO_ACTIVITY_HP_01) */
                COUNT(*) OVER () AS totalCount,
                pahp.TRANSACTION_DATE AS trxDate,
                pahp.PAYMENT_REFERENCE AS trxReference,
                pahp.TRANSACTION_DESCRIPTION AS trxDescription,
                CASE
                    WHEN pahp.TRANSACTION_TYPE = 'C' THEN pahp.TRANSACTION_AMOUNT ELSE NULL
                END AS credit,
                CASE
                    WHEN pahp.TRANSACTION_TYPE = 'D' THEN pahp.TRANSACTION_AMOUNT ELSE NULL
                END AS debit,
                pahp.TRANSACTION_TIME AS processingTime,
                pahp.DESCRIPTION_2 AS trxDescription2,
                pahp.STATUS AS trxStatus,
                pahp.TRANSACTION_TYPE AS trxType,
                pahp.SOURCE_CODE AS srcCode,
                pahp.TELLER_ID AS tellerId,
                pahp.TRANSACTION_CODE AS trxCode,
                pahp.CURRENCY_CODE AS currency,
                ROW_NUMBER() OVER (ORDER BY pahp.ACCOUNT_CHILD) AS row_num
            FROM
                (
                    SELECT
                        alhp.HP_ACCOUNT_NO AS acctNumber,
                        mv.corp_usr_id AS corpUserId,
                        mv.corp_id AS corpId
                    FROM MV_USER_ENTITLEMENT mv
                    INNER JOIN TBL_ACCT_LINK_BHP_HP alhp ON mv.acct_no = alhp.BHP_ACCOUNT_NO
                    WHERE mv.sub_product = 'HP'
                    AND alhp.BHP_ACCOUNT_NO = acctNumber
                    AND (NVL2(searchParam, mv.corp_short_nm, mv.corp_short_nm) LIKE NVL2(searchParam, '%'|| searchParam || '%', mv.corp_short_nm)
                        OR NVL2(searchParam, alhp.HP_ACCOUNT_NO, alhp.HP_ACCOUNT_NO) LIKE NVL2(searchParam, '%'|| searchParam || '%', alhp.HP_ACCOUNT_NO))
                    UNION ALL
                    SELECT
                        alhp.HP_ACCOUNT_NO AS acctNumber,
                        mv.corp_usr_id AS corpUserId,
                        mv.corp_id AS corpId
                    FROM MV_USER_ENTITLEMENT mv
                    INNER JOIN TBL_ACCT_LINK_BHP_HP alhp ON mv.acct_no = alhp.HP_ACCOUNT_NO
                    WHERE mv.sub_product = 'HP'
                    AND alhp.HP_ACCOUNT_NO = acctNumber
                    AND (NVL2(searchParam, mv.corp_short_nm, mv.corp_short_nm) LIKE NVL2(searchParam, '%'|| searchParam || '%', mv.corp_short_nm)
                        OR NVL2(searchParam, alhp.HP_ACCOUNT_NO, alhp.HP_ACCOUNT_NO) LIKE NVL2(searchParam, '%'|| searchParam || '%', alhp.HP_ACCOUNT_NO))
                ) temp
                INNER JOIN TBL_PORTFOLIO_ACTIVITY_HP pahp ON temp.acctNumber = pahp.ACCOUNT_CHILD
            WHERE
                temp.corpUserId = userId AND temp.corpId = corpId
                AND (pahp.TXN_DATETIME >= NVL(startDateTime, pahp.TXN_DATETIME) AND pahp.TXN_DATETIME <= NVL(endDateTime, pahp.TXN_DATETIME))
                AND (pahp.TRANSACTION_AMOUNT >= NVL(amtFrom, pahp.TRANSACTION_AMOUNT) AND pahp.TRANSACTION_AMOUNT <= NVL(amtTo, pahp.TRANSACTION_AMOUNT))
                AND pahp.TRANSACTION_CODE = NVL(trxCode, pahp.TRANSACTION_CODE)
        )
        WHERE row_num BETWEEN v_start_index AND v_end_index;

END HIRE_PURCHASE_TRX_HISTORY_LISTING;