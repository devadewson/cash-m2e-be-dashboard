create or replace NONEDITIONABLE PROCEDURE CORP_SUBSCRIBER_LINK_LIST (
    userId IN VARCHAR2,
    CURSOR OUT SYS_REFCURSOR
) AS
BEGIN
        OPEN CURSOR FOR
            SELECT
                corp.id,
                corp.corp_id as corpId,
                corp.corp_short_nm AS companyName,
                pvd.cd AS provider,
                cu.usr_id AS userId
            FROM tbl_corp_usr cu
            INNER JOIN tbl_corp corp ON cu.corp_id = corp.id
            INNER JOIN tbl_mt_pvd pvd ON corp.mt_pvd_id = pvd.id
            WHERE cu.usr_id = userId
            union
            select
                c.id,
                c.corp_id,
                c.corp_short_nm,
                from_comp.provider,
                from_comp.vu_userId
            from tbl_corp_link cl
            inner join tbl_corp c on cl.corp_id_to = c.id
            inner join tbl_corp_usr_group cug on c.id = cug.corp_id
            inner join (
            select
                corp.id,
                cu.usr_id,
                pvd.cd provider,
                vu.usr_id vu_userId
            from tbl_corp_usr cu
            inner join tbl_corp corp on cu.corp_id = corp.id
            inner join tbl_mt_pvd pvd on corp.mt_pvd_id = pvd.id
            left join tbl_corp_usr vu on cu.usr_id = vu.master_usr_id
            where cu.usr_id = userId
            ) from_comp on cl.corp_id_from = from_comp.id
            and cug.can_viewed_by = from_comp.provider;
END CORP_SUBSCRIBER_LINK_LIST;