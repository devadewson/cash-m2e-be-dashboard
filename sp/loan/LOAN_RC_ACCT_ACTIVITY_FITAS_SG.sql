create or replace NONEDITIONABLE PROCEDURE LOAN_RC_ACCT_ACTIVITY_FITAS_SG
(
    accountNumber IN NVARCHAR2,
    hostProductCode IN NVARCHAR2,
	corpId IN NVARCHAR2,
	userId IN NVARCHAR2,
	searchParam IN NVARCHAR2,
    startDateTime IN TIMESTAMP WITH TIME ZONE,
    endDateTime IN TIMESTAMP WITH TIME ZONE,
    amtFrom IN NUMBER,
    amtTo IN NUMBER,
    trxCode IN NVARCHAR2,
    pageNumber IN NUMBER,
	pageSize IN NUMBER,
	CURSOR OUT SYS_REFCURSOR
  )
AS
   v_start_index NUMBER;
   v_end_index NUMBER;
   v_acctType NVARCHAR2(10);
   v_startDateTime TIMESTAMP(6) WITH TIME ZONE;
   v_endDateTime TIMESTAMP(6) WITH TIME ZONE;
   v_amtFrom NUMBER;
   v_amtTo NUMBER;
BEGIN
	v_start_index := (pageNumber - 1) * pageSize + 1;
    v_end_index := pageNumber * pageSize;
    v_acctType := 'RC';

    SELECT DECODE(
        startDateTime, NULL, (SELECT MIN(DT_CREATED) FROM TBL_PORTFOLIO_ACTIVITY_STRC), startDateTime
    ) INTO v_startDateTime FROM DUAL;
    SELECT DECODE(
        endDateTime, NULL, (SELECT MAX(DT_CREATED) FROM TBL_PORTFOLIO_ACTIVITY_STRC), endDateTime
    ) INTO v_endDateTime FROM DUAL;

    SELECT DECODE(
        amtFrom, NULL, (SELECT MIN(AMOUNT) FROM TBL_PORTFOLIO_ACTIVITY_STRC), amtFrom
    ) INTO v_amtFrom FROM DUAL;
    SELECT DECODE(
        amtTo, NULL, (SELECT MAX(AMOUNT) FROM TBL_PORTFOLIO_ACTIVITY_STRC), amtTo
    ) INTO v_amtTo FROM DUAL;

OPEN CURSOR FOR
    SELECT * FROM (
        SELECT
            COUNT(id) OVER() as totalCount,
            ROW_NUMBER() OVER (ORDER BY acct_no ASC) as row_num,
            TO_CHAR(DT_CREATED, 'DD Mon YYYY HH24:MI') AS trxDate,
            TXN_REFERENCE AS trxReference,
            TRANSACTION_DESCRIPTION_1 AS trxDescription,
            AMOUNT AS amount
        FROM (
                SELECT
                    strc.id,
                    mv.acct_nm,
                    mv.acct_no,
                    strc.DT_CREATED,
                    strc.TXN_REFERENCE,
                    strc.TRANSACTION_DESCRIPTION_1,
                    strc.AMOUNT
                FROM
                    TBL_PORTFOLIO_ACTIVITY_STRC strc
                    INNER JOIN MV_USER_ENTITLEMENT mv ON mv.cif_no = strc.ACCOUNT_NO
                WHERE
                    mv.acct_no = accountNumber
                    and mv.corp_usr_id = userId
                    AND mv.corp_id = corpId
                    AND UPPER(TXN_REFERENCE) LIKE '%' || UPPER(trxCode) || '%'
                    AND AMOUNT BETWEEN v_amtFrom AND v_amtTo
                    AND DT_CREATED  BETWEEN v_startDateTime AND v_endDateTime
                    AND mv.product = hostProductCode
                    AND mv.sub_product = v_acctType
                    AND mv.provider = 'SG'
                    AND UPPER(strc.TRANSACTION_DESCRIPTION_1) LIKE '%' || UPPER(searchParam) || '%'
            )
    )
    WHERE row_num BETWEEN v_start_index AND v_end_index
    ORDER BY trxDate DESC;
END LOAN_RC_ACCT_ACTIVITY_FITAS_SG;