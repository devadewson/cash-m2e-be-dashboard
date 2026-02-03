create or replace NONEDITIONABLE PROCEDURE ACCT_PROD_ENTITLEMENT_CIF (
    userId    IN VARCHAR2,
    corpId    IN VARCHAR2,
    corpCifNo IN VARCHAR2,
    product   IN VARCHAR2,
    CURSOR OUT SYS_REFCURSOR
) AS

BEGIN
    OPEN CURSOR FOR
    with productCTE as (
        select distinct
            case
                when mv.is_add_oth_bank_acct = 'Y' then to_nchar('OTHER')
                else mv.product
            end as productCode,
            case
                when mv.is_add_oth_bank_acct = 'Y' then mv.product
                else mv.sub_product
            end as subProductCode,
            case
                when mv.is_add_oth_bank_acct = 'Y' then to_nchar('Other Bank')
                else mapt.dscp
            end as productDescription,
            case
                when mv.is_add_oth_bank_acct = 'Y' then mapt.dscp
                else mat.dscp
            end as subProductDescription
        from MV_USER_ENTITLEMENT mv
            inner join tbl_mt_acct_typ mat on mat.cd = mv.sub_product
            inner join tbl_mt_acct_prod_typ mapt on mapt.cd = mv.product
            where mv.corp_usr_id = userId
            and mv.corp_id = corpId
            and mv.cif_no = corpCifNo
            and (
                case
                    when mv.is_add_oth_bank_acct = 'Y' then to_nchar('OTHER')
                    else mv.product
                end
            ) = product

        union all

        select distinct
            case
                when trd.category in ('I', 'E', 'G', 'L') then to_nchar('TRADE')
            end as productCode,
            case
                when trd.category = 'I' then to_nchar('I')
                when trd.category = 'E' then to_nchar('E')
                when trd.category = 'G' then to_nchar('G')
                when trd.category = 'L' then to_nchar('L')
            end as subProductCode,
            case
                when trd.category in ('I', 'E', 'G', 'L') then to_nchar('Trade')
            end as productDescription,
            case
                when trd.category = 'I' then to_nchar('Import')
                when trd.category = 'E' then to_nchar('Export')
                when trd.category = 'G' then to_nchar('Guarantee')
                when trd.category = 'L' then to_nchar('Loan')
            end as subProductDescription
        from MV_USER_ENTITLEMENT mv
            inner join tbl_portfolio_trd trd on mv.cif_no = trd.cif_no
            where mv.corp_usr_id = userId
            and mv.corp_id = corpId
            and mv.cif_no = corpCifNo

        union all

        select distinct
            to_nchar('LOAN') as productCode,
            to_nchar('OD') as subProductCode,
            to_nchar('Loan') as productDescription,
            to_nchar('Overdraft') as subProductDescription
        from MV_USER_ENTITLEMENT mv
            inner join mv_portfolio_dp_acct dp on dp.account_no = mv.acct_no
            where mv.corp_usr_id = userId
            and mv.corp_id = corpId
            and mv.cif_no = corpCifNo
            and dp.facility_limit > 0
        )
        select * from ProductCTE where productCode = product;

END ACCT_PROD_ENTITLEMENT_CIF;