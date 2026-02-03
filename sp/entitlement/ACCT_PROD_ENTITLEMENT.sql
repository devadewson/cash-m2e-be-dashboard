create or replace PROCEDURE ACCT_PROD_ENTITLEMENT (
    userId IN VARCHAR2,
    corpId IN VARCHAR2,
    CURSOR OUT SYS_REFCURSOR
) AS

BEGIN

    OPEN CURSOR FOR
        select distinct
            case
                when mv.is_add_oth_bank_acct = 'Y' then to_nchar('OTHER')
                when trd.category in ('I', 'E', 'G', 'L') then to_nchar('TRADE')
                else mv.product
            end as productCode,
            case
                when mv.is_add_oth_bank_acct = 'Y' then mv.product
                when trd.category = 'I' then to_nchar('IMP')
                when trd.category = 'E' then to_nchar('EXP')
                when trd.category = 'G' then to_nchar('GRT')
                when trd.category = 'L' then to_nchar('LOAN')
                else mv.sub_product
            end as subProductCode,
            case
                when mv.is_add_oth_bank_acct = 'Y' then to_nchar('Other Bank')
                when trd.category in ('I', 'E', 'G', 'L') then to_nchar('Trade')
                else mapt.dscp
            end as productDescription,
            case
                when mv.is_add_oth_bank_acct = 'Y' then mapt.dscp
                when trd.category = 'I' then to_nchar('Import')
                when trd.category = 'E' then to_nchar('Export')
                when trd.category = 'G' then to_nchar('Guarantee')
                when trd.category = 'L' then to_nchar('Loan')
                else mat.dscp
            end as subProductDescription
        from MV_USER_ENTITLEMENT mv
            inner join tbl_mt_acct_typ@CTS_SSO mat on mat.cd = mv.sub_product
            inner join tbl_mt_acct_prod_typ@CTS_SSO mapt on mapt.cd = mv.product
            left join tbl_portfolio_trd trd on mv.cif_no = trd.cif_no
            where mv.corp_usr_id = userId and mv.corp_id = corpId

        union all

        select distinct
            to_nchar('LOAN') as productCode,
            to_nchar('OD') as subProductCode,
            to_nchar('Loan') as productDescription,
            to_nchar('Overdraft') as subProductDescription
        from MV_USER_ENTITLEMENT mv
            inner join tbl_mt_acct_typ@CTS_SSO mat on mat.cd = mv.sub_product
            inner join tbl_mt_acct_prod_typ@CTS_SSO mapt on mapt.cd = mv.product
            inner join mv_portfolio_dp_acct dp on dp.account_no = mv.acct_no
            where mv.corp_usr_id = userId and mv.corp_id = corpId and dp.facility_limit > 0;


END ACCT_PROD_ENTITLEMENT;