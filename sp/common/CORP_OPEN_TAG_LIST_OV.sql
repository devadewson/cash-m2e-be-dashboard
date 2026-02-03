create or replace NONEDITIONABLE PROCEDURE CORP_OPEN_TAG_LIST_OV(
    pageNumber IN NUMBER,
    pageSize IN NUMBER,
    userId IN VARCHAR2,
    corpId IN VARCHAR2,
    searchParam IN VARCHAR2,
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
        SELECT *
        FROM (
            SELECT
                all_corp.corp_short_nm AS companyName,
                all_corp.corp_id AS corpId,
                all_corp.cif_no AS corpCifNo,
                all_corp.pvd_cd AS provider,
                entitle.noOfAccounts AS noOfAccounts,
                CASE
                    WHEN COUNT(all_corp.corp_short_nm) OVER() = 1 THEN
                        CASE
                            WHEN (SELECT cif_no FROM tbl_corp@cts_sso WHERE corp_id = corpId) = all_corp.cif_no THEN 'true'
                            ELSE 'false'
                        END
                    ELSE 'false'
                END AS isSubscriberCompOnly,
                COUNT(all_corp.corp_short_nm) OVER() AS totalCount,
                ROW_NUMBER() OVER (ORDER BY UPPER(all_corp.corp_short_nm)) AS row_num
            FROM (
                SELECT
                    a.cif_no,
                    COUNT(DISTINCT a.acct_no) AS noOfAccounts
                FROM
                    tbl_corp@cts_sso c
                INNER JOIN
                    tbl_corp_usr_group@cts_sso cug ON c.id = cug.corp_id AND c.is_del = 'N' AND c.is_deactivated = 'N'
                INNER JOIN
                    tbl_corp_usr_group_usr@cts_sso cugu ON cug.id = cugu.corp_usr_group_id
                INNER JOIN
                    tbl_corp_usr_group_acct@cts_sso cuga ON cugu.corp_usr_group_id = cuga.corp_usr_group_id
                INNER JOIN
                    tbl_corp_accts@cts_sso ca ON cuga.corp_acct_id = ca.id
                INNER JOIN
                    tbl_acct@cts_sso a ON ca.acct_id = a.id AND a.is_del = 'N' AND a.is_deactivated = 'N'
                WHERE
                    cugu.corp_usr_id = userId
                    AND c.corp_id = corpId
                GROUP BY
                    a.cif_no, c.cif_no
                ORDER BY
                    a.cif_no
            ) entitle
            INNER JOIN (
                SELECT
                    corp_short_nm, corp_id, cif_no, pvd.cd AS pvd_cd
                FROM
                    tbl_cif@cts_sso c
                INNER JOIN
                    tbl_mt_pvd@cts_sso pvd ON c.mt_pvd_id = pvd.id
                WHERE
                    c.IS_DEL = 'N'
                    AND c.is_deactivated = 'N'
            ) all_corp ON entitle.cif_no = all_corp.cif_no
            AND (
                searchParam IS NULL OR (
                    UPPER(all_corp.corp_short_nm) LIKE '%' || UPPER(searchParam) || '%'
                    OR UPPER(all_corp.corp_id) LIKE '%' || UPPER(searchParam) || '%'
                )
            )
            ORDER BY UPPER(all_corp.corp_short_nm) ASC
        )
        WHERE row_num BETWEEN v_start_index AND v_end_index;
END CORP_OPEN_TAG_LIST_OV;