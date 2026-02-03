create or replace NONEDITIONABLE PROCEDURE DEPOSIT_CA_PAYMENT_DTL_TRX_REF (
    bankRefNo IN VARCHAR2,
    userId    IN VARCHAR2,
    corpId    IN VARCHAR2,
    CURSOR OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN CURSOR FOR
        SELECT
            pd.ADVICE_TYPE AS paymentType,
            pd.AMOUNT_CREDITED AS trxAmount,
            pd.PAYER_REFERENCE_NO AS bankRefNo,
            CASE
                WHEN pd.INCOMING_OUTGOING_FLAG = 'I' THEN 'Incoming'
                WHEN pd.INCOMING_OUTGOING_FLAG = 'O' THEN 'Outgoing'
                ELSE NULL
            END AS trxType,
            mv.acct_nm AS accountName,
            pd.ACCOUNT_NO AS accountNo,
            pd.REMITTER AS payerName,
            '<Payer Account Number>' AS payerAccount,
            '<Bank Code>' AS payerBankCode,
            '<Bank Name>' AS payerBankName,
            pd.VALUE_DATE AS trxDate,
            pd.TOTAL_CHARGES AS totalCharges,
            '-' AS vatAmount,
            pd.AMOUNT_CREDITED AS creditAmount,
            pd.PAYMENT_DETAILS AS paymentDetail,
            pd.VALUE_DATE AS valueDate,
            pd.CHEQUE_NO AS chequeNo,
            pd.UETR_NO AS uetrNo,
            pd.SENDER_DETAILS AS senderName,
            'BOTKSGSX' AS bank,
            pd.CURRENCY AS currency
        FROM
            TBL_PAYMENT_DETAIL pd
            INNER JOIN MV_USER_ENTITLEMENT mv
            ON mv.acct_no = pd.ACCOUNT_NO
        WHERE
            mv.corp_usr_id = userId AND mv.corp_id = corpId
            AND pd.PAYER_REFERENCE_NO = bankRefNo;
END DEPOSIT_CA_PAYMENT_DTL_TRX_REF;