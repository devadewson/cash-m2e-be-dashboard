create or replace PROCEDURE LOAN_RC_ACCT_LIST
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
   product NVARCHAR2(10);
   acctType NVARCHAR2(10);
BEGIN
	v_start_index := (pageNumber - 1) * pageSize + 1;
    v_end_index := pageNumber * pageSize;
    product := 'LOAN';
    acctType := 'RC';

OPEN CURSOR FOR
    SELECT * FROM (
        SELECT
            COUNT(acct_no) OVER() as totalCount,
            ROW_NUMBER() OVER (ORDER BY acct_no ASC) as row_num,
            acct_nm AS accountName,
            acct_no AS accountNumber,
            cif_no as cifNumber,
            CURRENCY_CODE AS accountCurrency,
            FACILITY_LIMIT AS drawdownAmount,
            OUTSTANDING_BALANCE  AS outstandingBalance,
            DT_CREATED AS dateCreated
        FROM (
            SELECT
                acct_nm,
                acct_no,
                cif_no,
                CURRENCY_CODE,
                FACILITY_LIMIT,
                OUTSTANDING_BALANCE,
                DT_CREATED,
                ROW_NUMBER() OVER (PARTITION BY acct_no ORDER BY DT_CREATED DESC) AS rn
            FROM (
                SELECT
                    mv.acct_nm,
                    mv.acct_no,
                    mv.cif_no,
                    strc.CURRENCY_CODE,
                    strc.FACILITY_LIMIT,
                    strc.OUTSTANDING_BALANCE,
                    strc.DT_CREATED
                FROM
                    TBL_PORTFOLIO_STRC strc
                    INNER JOIN MV_USER_ENTITLEMENT mv ON mv.cif_no = strc.CIF_NO
                WHERE
                    mv.corp_usr_id = userId
                    AND mv.corp_id = corpId
                    AND mv.product = product
                    AND mv.sub_product = acctType
                    AND UPPER(mv.cif_no) LIKE '%' || UPPER(searchParam) || '%'
            UNION
                SELECT
                    mv.acct_nm,
                    mv.acct_no,
                    mv.cif_no,
                    strc.CURRENCY_CODE,
                    strc.FACILITY_LIMIT,
                    strc.OUTSTANDING_BALANCE,
                    strc.DT_CREATED
                FROM
                    TBL_PORTFOLIO_STRC strc
            INNER JOIN MV_USER_ENTITLEMENT mv ON mv.cif_no = strc.CIF_NO
            WHERE
                mv.corp_usr_id = userId
                AND mv.corp_id = corpId
                AND mv.product = product
                AND mv.sub_product = acctType
                AND UPPER(mv.acct_nm) LIKE '%' || UPPER(searchParam) || '%'
            )
        )
        WHERE rn = 1
    )
    WHERE row_num BETWEEN v_start_index AND v_end_index
    ORDER BY accountName;
END LOAN_RC_ACCT_LIST;