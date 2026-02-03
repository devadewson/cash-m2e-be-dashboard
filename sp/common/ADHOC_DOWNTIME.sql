create or replace NONEDITIONABLE PROCEDURE adhoc_downtime
(
    userId IN VARCHAR2,
    timeNow IN TIMESTAMP,
    CURSOR OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN CURSOR FOR
    SELECT
        cat.cd        AS downtimeCategory,
        pvd.cd        AS downtimeprovider,
        app.cd        AS downtimeApp,
        prod.cd       AS downtimeProduct,
        sub.cd        AS downtimeSubProduct,
        cu.sys_pvd_cd AS provider
    FROM
        tbl_mt_adhoc_downtime@cts_sso dt
        LEFT JOIN tbl_mt_pvd@cts_sso            pvd ON dt.mt_pvd_id = pvd.id
        LEFT JOIN tbl_corp_usr@cts_sso          cu ON cu.sys_pvd_cd = pvd.cd
        LEFT JOIN tbl_mt_category@cts_sso       cat ON cat.id = dt.mt_category_id
        LEFT JOIN tbl_mt_app@cts_sso            app ON app.id = dt.mt_app_id
        LEFT JOIN tbl_mt_prod@cts_sso           prod ON prod.id = dt.mt_prod_id
        LEFT JOIN tbl_mt_sub_prod_cv@cts_sso    sub ON sub.id = dt.mt_sub_prod_cv_id
    WHERE
            cu.usr_id = userId
        AND dt.start_date_time <= timeNow
        AND dt.end_date_time >= timeNow
        AND dt.is_disable = 'N'
    UNION
    SELECT
        cat.cd        AS downtimeCategory,
        pvd.cd        AS downtimeprovider,
        app.cd        AS downtimeApp,
        prod.cd       AS downtimeProduct,
        sub.cd        AS downtimeSubProduct,
        null AS provider
    FROM
        tbl_mt_adhoc_downtime@cts_sso dt
        LEFT JOIN tbl_mt_category@cts_sso       cat ON cat.id = dt.mt_category_id
        LEFT JOIN tbl_mt_pvd@cts_sso            pvd ON pvd.id = dt.mt_pvd_id
        LEFT JOIN tbl_mt_app@cts_sso            app ON app.id = dt.mt_app_id
        LEFT JOIN tbl_mt_prod@cts_sso           prod ON prod.id = dt.mt_prod_id
        LEFT JOIN tbl_mt_sub_prod_cv@cts_sso    sub ON sub.id = dt.mt_sub_prod_cv_id
    WHERE
            dt.start_date_time <= timeNow
        AND dt.end_date_time >= timeNow
        AND dt.is_disable = 'N'
        AND cat.cd = 'GLOBAL';
END adhoc_downtime;