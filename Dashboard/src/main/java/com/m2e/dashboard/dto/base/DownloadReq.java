package com.m2e.dashboard.dto.base;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.m2e.dashboard.dto.deposit.ca.request.download.*;
import com.m2e.dashboard.dto.deposit.fd.request.download.DepositFDAccountListingDownloadReqDTO;
import com.m2e.dashboard.dto.deposit.fd.request.download.DepositFDCertificateListingDownloadReqDTO;
import com.m2e.dashboard.dto.deposit.fd.request.download.DepositFDTrxHistoryDownloadReqDTO;
import com.m2e.dashboard.dto.deposit.va.request.DepositVADownloadReqDTO;
import com.m2e.dashboard.dto.deposit.va.request.DepositVATransHistDownloadReqDTO;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceListingDownloadReqDTO;
import com.m2e.dashboard.dto.loan.overdraft.request.LNOverdraftAcctListingDownloadReqDTO;
import com.m2e.dashboard.dto.loan.rc.request.download.LNRevolvingCreditAcctListingDownloadReqDTO;
import com.m2e.dashboard.dto.loan.rc.request.download.LNRevolvingCreditTrxHistoryDownloadReqDTO;
import com.m2e.dashboard.dto.loan.tlf.request.download.LNTLFAcctListingDownloadReqDTO;
import com.m2e.dashboard.dto.loan.tlf.request.download.LNTLFTranchesDownloadReqDTO;
import com.m2e.dashboard.dto.loan.tlf.request.download.LNTLFTrxHistoryDownloadReqDTO;
import com.m2e.dashboard.dto.otherbank.request.deposit.download.OtherBankDepositAcctListingDownloadReqDTO;
import com.m2e.dashboard.dto.otherbank.request.deposit.download.OtherBankDepositTrxHistoryDownloadReqDTO;
import com.m2e.dashboard.dto.otherbank.request.loan.download.OtherBankLoanAcctListDownloadReqDTO;
import com.m2e.dashboard.dto.otherbank.request.loan.download.OtherBankLoanTrxHistoryDownloadReqDTO;
import com.m2e.dashboard.dto.overview.request.OverviewDownloadReqDTO;
import com.m2e.dashboard.dto.trade.request.download.TradeAccountListingDownloadReqDTO;
import com.m2e.dashboard.enums.ReportFormat;
import com.m2e.dashboard.enums.ReportType;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "reportType",
        visible = true
)
@JsonSubTypes({
        //Deposit
        @JsonSubTypes.Type(value = DepositCAAcctListDownloadReqDTO.class, name = "DEPOSIT_CA_ACCOUNTLISTING_RPT_TYPE"),
        @JsonSubTypes.Type(value = DepositCATrxHistDownloadReqDTO.class, name = "DEPOSIT_CA_TRX_HISTORY_RPT_TYPE"),
        @JsonSubTypes.Type(value = DepositCAPaymentHistDownloadReqDTO.class, name = "DEPOSIT_CA_PAYMENT_HISTORY_RPT_TYPE"),
        @JsonSubTypes.Type(value = DepositCAPaymentDetailsDownloadReqDTO.class, name = "DEPOSIT_CA_PAYMENT_DETAIL_RPT_TYPE"),
        @JsonSubTypes.Type(value = DepositCAEODBalanceDownloadReqDTO.class, name = "DEPOSIT_CA_EOD_BALANCE_RPT_TYPE"),
        @JsonSubTypes.Type(value = DepositCAMT103DownloadReqDTO.class, name = "DEPOSIT_CA_SWIFT_MT103_RPT_TYPE"),
        @JsonSubTypes.Type(value = DepositCAGPIStatusDownloadReqDTO.class, name = "DEPOSIT_CA_GPI_STATUS_RPT_TYPE"),
        @JsonSubTypes.Type(value = DepositFDAccountListingDownloadReqDTO.class, name = "DEPOSIT_FD_ACCLISTING_RPT_TYPE"),
        @JsonSubTypes.Type(value = DepositFDCertificateListingDownloadReqDTO.class, name = "DEPOSIT_FD_CERTLISTING_RPT_TYPE"),
        @JsonSubTypes.Type(value = DepositFDTrxHistoryDownloadReqDTO.class, name = "DEPOSIT_FD_TRXHISTORIES_RPT_TYPE"),
        @JsonSubTypes.Type(value = DepositVADownloadReqDTO.class, name = "DEPOSIT_VA_ACCOUNTLISTING_RPT_TYPE"),
        @JsonSubTypes.Type(value = DepositVADownloadReqDTO.class, name = "DEPOSIT_LINKED_VA_ACCOUNT_LISTING_RPT_TYPE"),
        @JsonSubTypes.Type(value = DepositVATransHistDownloadReqDTO.class, name = "DEPOSIT_VA_TRXHISTORIES_RPT_TYPE"),
//        //Investment
//        @JsonSubTypes.Type(value = INVUnitTrustAccDownloadReqDTO.class, name = "UNIT_TRUST_ACCOUNTLISTING_RPT_TYPE"),
//        @JsonSubTypes.Type(value = INVUnitTrustFundDownloadReqDTO.class, name = "UNIT_TRUST_FUND_LISTING_RPT_TYPE"),
//        @JsonSubTypes.Type(value = INVUnitTrustHistDownloadReqDTO.class, name = "UNIT_TRUST_TRXHISTORIES_RPT_TYPE"),
//        //Auto Finance
//        @JsonSubTypes.Type(value = AFHPAcctListDownloadReq.class, name = "AF_HP_ACCOUNT_LISTING_RPT_TYPE"),
//        @JsonSubTypes.Type(value = AFHPTrxHistDownloadReq.class, name = "AF_HP_TRX_HISTORY_RPT_TYPE"),
//        @JsonSubTypes.Type(value = AFFSAcctListDownloadReqDTO.class, name = "AF_FS_ACCOUNT_LISTING_RPT_TYPE"),
//        @JsonSubTypes.Type(value = AFFSSubAcctListDownloadReqDTO.class, name = "AF_FS_SUB_ACCOUNT_LISTING_RPT_TYPE"),
//        @JsonSubTypes.Type(value = AFFSTrxHistDownloadReqDTO.class, name = "AF_FS_TRX_HISTORY_RPT_TYPE"),
//        @JsonSubTypes.Type(value = AFBDAcctListDownloadReqDTO.class, name = "AF_BD_ACCOUNT_LISTING_RPT_TYPE"),
//        @JsonSubTypes.Type(value = AFBDSubAcctListDownloadReqDTO.class, name = "AF_BD_SUB_ACCOUNT_LISTING_RPT_TYPE"),
//        @JsonSubTypes.Type(value = AFBDTrxHistoryDownloadReqDTO.class, name = "AF_BD_TRX_HISTORY_RPT_TYPE"),
//        @JsonSubTypes.Type(value = AFPBRAccountListingDownloadReq.class, name = "AF_PBR_ACCLISTING_RPT_TYPE"),
//        @JsonSubTypes.Type(value = AFPBRSubaccountListingDownloadReq.class, name = "AF_PBR_SUBACCLISTING_RPT_TYPE"),
//        @JsonSubTypes.Type(value = AFPBRTrxHistoryDownloadReq.class, name = "AF_PBR_TRXHISTORIES_RPT_TYPE"),
//        //Credit Card
//        @JsonSubTypes.Type(value = CCCreditCardListDLReqDTO.class, name = "CREDIT_CARD_LISTING_RPT_TYPE"),
//        @JsonSubTypes.Type(value = CCCreditCardTrxHistDLReqDTO.class, name = "CREDIT_CARD_TRXHISTORIES_RPT_TYPE"),
//        @JsonSubTypes.Type(value = CCCreditCardPendHistDLReqDTO.class, name = "CREDIT_CARD_PENDING_TRX_RPT_TYPE"),
//        @JsonSubTypes.Type(value = CCCreditCardMasterListDLReqDTO.class, name = "CREDIT_CARD_MASTER_LISTING_RPT_TYPE"),
//        @JsonSubTypes.Type(value = CCChargeCardListDLReqDTO.class, name = "CHARGE_CARD_LISTING_RPT_TYPE"),
//        @JsonSubTypes.Type(value = CCChargeCardTrxHistDLReqDTO.class, name = "CHARGE_CARD_TRXHISTORIES_RPT_TYPE"),
//        @JsonSubTypes.Type(value = CCChargeCardPendHistDLReqDTO.class, name = "CHARGE_CARD_PENDING_TRX_RPT_TYPE"),
//        @JsonSubTypes.Type(value = CCDebitCardListDLReqDTO.class, name = "DEBIT_CARD_LISTING_RPT_TYPE"),
//        //Trade
        @JsonSubTypes.Type(value = TradeAccountListingDownloadReqDTO.class, name = "TRADE_ACCOUNTLISTING_RPT_TYPE"),
        //Other Bank
        @JsonSubTypes.Type(value = OtherBankDepositAcctListingDownloadReqDTO.class, name = "OTHER_BANK_DEPOSIT_ACCOUNT_LISTING_RPT_TYPE"),
        @JsonSubTypes.Type(value = OtherBankDepositTrxHistoryDownloadReqDTO.class, name = "OTHER_BANK_TRXHISTORIES_RPT_TYPE"),
        @JsonSubTypes.Type(value = OtherBankLoanAcctListDownloadReqDTO.class, name = "OTHER_BANK_LOAN_ACCOUNT_LISTING_RPT_TYPE"),
        @JsonSubTypes.Type(value = OtherBankLoanTrxHistoryDownloadReqDTO.class, name = "OTHER_BANK_LOAN_TRXHISTORIES_RPT_TYPE"),
        //Loan
        @JsonSubTypes.Type(value = LNOverdraftAcctListingDownloadReqDTO.class, name = "LOAN_OD_ACCOUNTLISTING_RPT_TYPE"),
        @JsonSubTypes.Type(value = LNRevolvingCreditAcctListingDownloadReqDTO.class, name = "LOAN_RC_ACCOUNTLISTING_RPT_TYPE"),
        @JsonSubTypes.Type(value = LNTLFAcctListingDownloadReqDTO.class, name = "LOAN_TLF_ACCOUNTLISTING_RPT_TYPE"),
        @JsonSubTypes.Type(value = LNRevolvingCreditTrxHistoryDownloadReqDTO.class, name = "LOAN_RC_TRXHISTORIES_RPT_TYPE"),
        @JsonSubTypes.Type(value = LNTLFTrxHistoryDownloadReqDTO.class, name = "LOAN_TLF_TRXHISTORIES_RPT_TYPE"),
        @JsonSubTypes.Type(value = LNTLFTranchesDownloadReqDTO.class, name = "LOAN_TLF_TRANCHES_RPT_TYPE"),
        //Portfolio Overview
        @JsonSubTypes.Type(value = OverviewDownloadReqDTO.class, name = "OVERVIEW_LISTING_RPT_TYPE"),
        @JsonSubTypes.Type(value = EInvoiceListingDownloadReqDTO.class, name = "EINVOICE_LISTING_RPT_TYPE"),
})
//@Schema(
//        description = "Base request for downloading files",
//        discriminatorProperty = "type",
//        discriminatorMapping = {
//                @DiscriminatorMapping(value = "AFHPTrxHistDownloadReq", schema = AFHPTrxHistDownloadReq.class)
//        }
//)
public class DownloadReq {
    private ReportFormat format;
    private ReportType reportType;
    private Boolean realTimeIndicator;
}