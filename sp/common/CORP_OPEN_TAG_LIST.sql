create or replace NONEDITIONABLE PROCEDURE CORP_OPEN_TAG_LIST
(
      pageNumber IN NUMBER,
      pageSize IN NUMBER,
      userId IN VARCHAR2,
      corpId IN VARCHAR2,
      searchKey IN VARCHAR2,
      acctType IN VARCHAR2,
      productCode IN VARCHAR2,
      CURSOR OUT SYS_REFCURSOR
) AS
     v_total_page NUMBER;
     v_record_size NUMBER;
     v_start_index NUMBER;
     v_end_index NUMBER;
BEGIN
        -- calculate pagination index
        v_start_index := (pageNumber - 1) * pageSize + 1;
        v_end_index := pageNumber * pageSize;
        OPEN CURSOR FOR
        SELECT * FROM (
        SELECT all_corp.corp_short_nm AS companyName,
        all_corp.corp_id AS corpId,
        pvd.cd AS provider,
        corp_info.noOfAccounts as noOfAccounts,
        COUNT(all_corp.corp_short_nm) OVER() as totalCount,
        ROW_NUMBER() OVER (ORDER BY UPPER(all_corp.corp_short_nm)) as row_num
        FROM tbl_corp all_corp
        INNER JOIN tbl_mt_pvd pvd ON all_corp.mt_pvd_id = pvd.id
        INNER JOIN (
        SELECT a.cif_no, COUNT(a.id) AS noOfAccounts
        FROM tbl_corp c
        INNER JOIN tbl_corp_usr_group cug ON c.id = cug.corp_id
        INNER JOIN tbl_corp_usr_group_usr cugu ON cug.id = cugu.corp_usr_group_id
        INNER JOIN tbl_corp_usr_group_acct cuga ON cugu.corp_usr_group_id = cuga.corp_usr_group_id
        INNER JOIN tbl_corp_accts ca ON cuga.corp_acct_id = ca.id
        INNER JOIN tbl_acct a ON ca.acct_id = a.id
        WHERE cugu.corp_usr_id = userId and c.corp_id = corpId and a.acct_typ = acctType and a.prod_cd = productCode
        GROUP BY a.cif_no
) corp_info ON all_corp.cif_no = corp_info.cif_no where searchKey is null OR (length(searchKey) >= 3 and (UPPER(all_corp.corp_short_nm) LIKE '%' || UPPER(searchKey) || '%' OR UPPER(all_corp.corp_id) LIKE '%' || UPPER(searchKey) || '%'))
ORDER BY UPPER(all_corp.corp_short_nm) ASC)
WHERE row_num BETWEEN v_start_index AND v_end_index;
END CORP_OPEN_TAG_LIST;