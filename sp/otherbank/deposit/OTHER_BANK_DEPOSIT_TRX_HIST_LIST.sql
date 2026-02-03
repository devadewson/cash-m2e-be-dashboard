create or replace PROCEDURE OTHER_BANK_DEPOSIT_TRX_HIST_LIST (
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
            SELECT COUNT(*) OVER() AS totalCount,
				TO_CHAR(pas.dt_created, 'DD Mon YYYY HH:MI') AS "date",
				pas.stat_ref_no AS trxRefNo,
				pas.stat_pay_description_1 AS trxDesc,
				pas.debit_amount AS debit,
				pas.credit_amount AS credit,
				pas.stat_entry_date AS entryDate,
                pas.stat_trn_type_indicator_code AS trxType,
                pas.mbb_trn_code AS trxCode,
                pas.clo_bal_amount AS closingBalance,
                pas.stat_amount AS trxAmount,
                '' AS supplementaryDetails,
                pas.ope_bal_amount AS openingBalance,
                mv.swift_cd AS swiftTrxCode,
                pas.ope_bal_curr AS currency,
				ROW_NUMBER() OVER (ORDER BY pas.account_id) AS row_num
			FROM tbl_portfolio_activity_swift pas
            INNER JOIN MV_USER_ENTITLEMENT mv ON mv.acct_no = pas.account_id
            WHERE
                mv.corp_usr_id = userId AND mv.corp_id = corpId
                AND pas.account_id = acctNumber
            )
		WHERE row_num BETWEEN v_start_index AND v_end_index;
END OTHER_BANK_DEPOSIT_TRX_HIST_LIST;