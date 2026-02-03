create or replace PROCEDURE LOAN_TLF_ACCT_LIST
(
	corpId IN NVARCHAR2,
	userId IN NVARCHAR2,
	searchParam IN NVARCHAR2,
    pageNumber IN NUMBER,
	pageSize IN NUMBER,
	CURSOR OUT SYS_REFCURSOR
  )
AS
   v_start_index NUMBER;
   v_end_index NUMBER;
   acctType NVARCHAR2(10);
BEGIN
	v_start_index := (pageNumber - 1) * pageSize + 1;
    v_end_index := pageNumber * pageSize;
    acctType := 'TL';

OPEN CURSOR FOR
    SELECT * FROM (
        SELECT
            COUNT(acct_no) OVER() as totalCount,
            ROW_NUMBER() OVER (ORDER BY acct_no ASC) as row_num,
            acct_nm AS accountName,
            acct_no AS accountNumber,
            cif_no as cifNumber,
            CURRENCY_CODE AS accountCurrency,
            DRAWDOWN_AMOUNT AS drawdownAmount,
            OUTSTANDING_BALANCE  AS outstandingBalance,
            DT_CREATED AS dateCreated,
            IS_OVERDUE AS isOverdue
        FROM (
            SELECT
                acct_nm,
                acct_no,
                cif_no,
                CURRENCY_CODE,
                DRAWDOWN_AMOUNT,
                OUTSTANDING_BALANCE,
                DT_CREATED,
                IS_OVERDUE,
                ROW_NUMBER() OVER (PARTITION BY acct_no ORDER BY DT_CREATED DESC) AS rn
            FROM (
                SELECT
                    mv.acct_nm,
                    mv.acct_no,
                    mv.cif_no,
                    ln.CURRENCY_CODE,
                    ln.DRAWDOWN_AMOUNT,
                    ln.OUTSTANDING_BALANCE,
                    ln.DT_CREATED,
                    CASE
                        WHEN SYSDATE > TO_DATE(ln.PRINCIPAL_PAY_DUE_DATE, 'YYYYMMDD') THEN 'Y'
                        ELSE 'N'
                    END AS IS_OVERDUE
                FROM
                    TBL_PORTFOLIO_LN ln
                    INNER JOIN MV_USER_ENTITLEMENT mv ON mv.acct_no = ln.ACCOUNT_NO
                WHERE
                    UPPER(mv.provider) <> 'SG'
                    AND mv.corp_usr_id = userId
                    AND mv.corp_id = corpId
                    AND mv.sub_product = acctType
                    AND UPPER(mv.acct_no) LIKE '%' || UPPER(searchParam) || '%'
            UNION
                SELECT
                    mv.acct_nm,
                    mv.acct_no,
                    mv.cif_no,
                    ln.CURRENCY_CODE,
                    ln.DRAWDOWN_AMOUNT,
                    ln.OUTSTANDING_BALANCE,
                    ln.DT_CREATED,
                    CASE
                        WHEN SYSDATE > TO_DATE(ln.PRINCIPAL_PAY_DUE_DATE, 'YYYYMMDD') THEN 'Y'
                        ELSE 'N'
                    END AS IS_OVERDUE
                FROM
                    TBL_PORTFOLIO_LN ln
            INNER JOIN MV_USER_ENTITLEMENT mv ON mv.acct_no = ln.ACCOUNT_NO
            WHERE
                UPPER(mv.provider) <> 'SG'
                AND mv.corp_usr_id = userId
                AND mv.corp_id = corpId
                AND mv.sub_product = acctType
                AND UPPER(mv.acct_nm) LIKE '%' || UPPER(searchParam) || '%'
            )

            UNION

            SELECT
                acct_nm,
                acct_no,
                cif_no,
                CURRENCY_CODE,
                DRAWDOWN_AMOUNT,
                OUTSTANDING_BALANCE,
                DT_CREATED,
                IS_OVERDUE,
                ROW_NUMBER() OVER (PARTITION BY acct_no ORDER BY DT_CREATED DESC) AS rn
            FROM (
                SELECT
                    mv.acct_nm,
                    mv.acct_no,
                    mv.cif_no,
                    ln.CURRENCY_CODE,
                    ln.DRAWDOWN_AMOUNT,
                    ln.OUTSTANDING_BALANCE,
                    ln.DT_CREATED,
                    CASE
                        WHEN SYSDATE > TO_DATE(ln.PRINCIPAL_PAY_DUE_DATE, 'YYYYMMDD') THEN 'Y'
                        ELSE 'N'
                    END AS IS_OVERDUE
                FROM
                    TBL_PORTFOLIO_LN ln
                    INNER JOIN MV_USER_ENTITLEMENT mv ON mv.cif_no = ln.cif_number
                WHERE
                    UPPER(mv.provider) = 'SG'
                    AND mv.corp_usr_id = userId
                    AND mv.corp_id = corpId
                    AND mv.sub_product = acctType
                    AND UPPER(mv.acct_no) LIKE '%' || UPPER(searchParam) || '%'
            UNION
                SELECT
                    mv.acct_nm,
                    mv.acct_no,
                    mv.cif_no,
                    ln.CURRENCY_CODE,
                    ln.DRAWDOWN_AMOUNT,
                    ln.OUTSTANDING_BALANCE,
                    ln.DT_CREATED,
                    CASE
                        WHEN SYSDATE > TO_DATE(ln.PRINCIPAL_PAY_DUE_DATE, 'YYYYMMDD') THEN 'Y'
                        ELSE 'N'
                    END AS IS_OVERDUE
                FROM
                    TBL_PORTFOLIO_LN ln
            INNER JOIN MV_USER_ENTITLEMENT mv ON mv.cif_no = ln.cif_number
            WHERE
                UPPER(mv.provider) = 'SG'
                AND mv.corp_usr_id = userId
                AND mv.corp_id = corpId
                AND mv.sub_product = acctType
                AND UPPER(mv.acct_nm) LIKE '%' || UPPER(searchParam) || '%'
            )
        )
        WHERE rn = 1
    )
    WHERE row_num BETWEEN v_start_index AND v_end_index
    ORDER BY accountName;
END LOAN_TLF_ACCT_LIST;