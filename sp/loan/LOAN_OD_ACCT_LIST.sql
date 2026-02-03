create or replace PROCEDURE LOAN_OD_ACCT_LIST(
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

OPEN CURSOR FOR
    SELECT * FROM (
        SELECT
            COUNT(acct_no) OVER() as totalCount,
            ROW_NUMBER() OVER (ORDER BY acct_no ASC) as row_num,
            acct_nm AS accountName,
            acct_no AS accountNumber,
            CURRENCY_CODE AS accountCurrency,
            DECODE(TEMPORARY_OD,
                0, 'Normal',
                'Intraday'
            ) AS overdraftType,
            DECODE(TEMPORARY_OD,
                0, FACILITY_LIMIT,
                TEMPORARY_OD
            ) AS overdraftLimit,
            UTILIZED_AMOUNT  AS utilisedAmount,
            DT_CREATED AS dateCreated
        FROM (
            SELECT
                ACCT_NM,
                ACCT_NO,
                CURRENCY_CODE,
                FACILITY_LIMIT,
                UTILIZED_AMOUNT,
                DT_CREATED,
                TEMPORARY_OD,
                ROW_NUMBER() OVER (PARTITION BY acct_no ORDER BY DT_CREATED DESC) AS rn
            FROM (
                SELECT
                    mv.ACCT_NM,
                    mv.ACCT_NO,
                    od.CURRENCY_CODE,
                    od.FACILITY_LIMIT,
                    od.UTILIZED_AMOUNT,
                    od.DT_CREATED,
                    od.TEMPORARY_OD
                FROM
                    TBL_PORTFOLIO_DP od
                    INNER JOIN MV_USER_ENTITLEMENT mv ON mv.acct_no = od.ACCOUNT_NO
                WHERE
                    mv.corp_usr_id = userId
                    AND mv.corp_id = corpId
                    AND od.IS_OD = 'Y'
                    AND UPPER(mv.cif_no) LIKE '%' || UPPER(searchParam) || '%'
            UNION
                SELECT
                    mv.ACCT_NM,
                    mv.ACCT_NO,
                    od.CURRENCY_CODE,
                    od.FACILITY_LIMIT,
                    od.UTILIZED_AMOUNT,
                    od.DT_CREATED,
                    od.TEMPORARY_OD
                FROM
                    TBL_PORTFOLIO_DP od
            INNER JOIN MV_USER_ENTITLEMENT mv ON mv.acct_no = od.ACCOUNT_NO
            WHERE
                mv.corp_usr_id = userId
                AND mv.corp_id = corpId
                AND od.IS_OD = 'Y'
                AND UPPER(mv.acct_nm) LIKE '%' || UPPER(searchParam) || '%'
            )
        )
        WHERE rn = 1
    )
    WHERE row_num BETWEEN v_start_index AND v_end_index
    ORDER BY accountName;
END LOAN_OD_ACCT_LIST;