create or replace PROCEDURE VA_ACCT_STATS
(
  corpId IN VARCHAR2,
  userId IN VARCHAR2,
  corpCifNo IN VARCHAR2,
  CURSOR OUT SYS_REFCURSOR
) AS
BEGIN
OPEN CURSOR FOR
  		SELECT COUNT(ut.PARENT_ACCOUNT) as totNoOfAccounts,
		COUNT(ut.id) as totalVa
		from TBL_ACCT_VA@CTS_SSO ut
        inner join MV_USER_ENTITLEMENT mv on ut.PARENT_ACCOUNT = mv.ACCT_NO
        where mv.CORP_USR_ID = userId
         --and (mv.CORP_ID = corpId OR corpId IS NULL)
        and (mv.Cif_no = corpCifNo OR corpCifNo IS NULL) ;

END VA_ACCT_STATS;