CREATE OR REPLACE NONEDITIONABLE PROCEDURE CUSTOMIZE_FIELD (
    userId      IN VARCHAR2,
    subProduct  IN VARCHAR2,
    screen      IN VARCHAR2,
    attribute   IN VARCHAR2,
    CURSOR      OUT SYS_REFCURSOR
) AS
    rows_impacted NUMBER := 0;
BEGIN
    -- If attribute is NULL or empty, delete the record
    IF attribute IS NULL OR attribute = '' THEN
        DELETE FROM TBL_PREF@CTS_SSO
        WHERE SUB_PRODUCT = subProduct
        AND SCREEN = screen
        AND USR_ID = userId;

        -- Capture the number of rows deleted
        rows_impacted := SQL%ROWCOUNT;

        COMMIT;

        -- Return the result message for delete
        OPEN CURSOR FOR
        SELECT 'Records deleted: ' || rows_impacted AS message
        FROM DUAL;

    ELSE
        -- Use a MERGE statement to insert or update based on existing records
        MERGE INTO TBL_PREF@CTS_SSO target
        USING (SELECT subProduct AS sub_product, screen AS screen_id, userId AS usr_id, attribute AS attribute FROM DUAL) source
        ON (target.SUB_PRODUCT = source.sub_product AND target.SCREEN = source.screen_id AND target.USR_ID = source.usr_id)
        WHEN MATCHED THEN
            -- If a match is found, update the record
            UPDATE SET ATTRIBUTE = source.attribute
        WHEN NOT MATCHED THEN
            -- If no match is found, insert a new record
            INSERT (ID, SUB_PRODUCT, SCREEN, USR_ID, ATTRIBUTE)
            VALUES (sys_guid(), source.sub_product, source.screen_id, source.usr_id, source.attribute);

        -- Determine whether an insert or update occurred
        IF SQL%ROWCOUNT = 1 THEN
            rows_impacted := 1; -- Since this is a new row
        ELSE
            rows_impacted := SQL%ROWCOUNT; -- Existing rows updated
        END IF;

        COMMIT;

        -- Return the result message for update/insert
        OPEN CURSOR FOR
        SELECT 'Records insert or update: ' || rows_impacted AS message
        FROM DUAL;
    END IF;

END CUSTOMIZE_FIELD;
