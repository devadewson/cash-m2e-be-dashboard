package com.m2e.dashboard.jms.deposit.ca.response.gpistatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBDepositCAGPIStatusPaymentEventDTO implements Serializable {
    @JsonProperty("network_reference")
    private String networkReference;

    @JsonProperty("message_name_identification")
    private String messageNameIdentification;

    @JsonProperty("business_service")
    private String businessService;

    @JsonProperty("tracker_event_type")
    private String trackerEventType;

    private Boolean valid;

    @JsonProperty("invalidity_reason")
    private String invalidityReason;

    @JsonProperty("invalidity_description")
    private String invalidityDescription;

    @JsonProperty("instruction_identification")
    private String instructionIdentification;

    @JsonProperty("case_identification")
    private String caseIdentification;

    @JsonProperty("original_instruction_identification")
    private String originalInstructionIdentification;

    @JsonProperty("assignment_identification")
    private String assignmentIdentification;

    @JsonProperty("transaction_status")
    private ESBDepositCAGPIStatusTrxnStatusDTO transactionStatus;

    @JsonProperty("return")
    private String returnStatus;

    @JsonProperty("underlying_cancellation_details")
    private ESBDepositCAGPIStatusUnderlyingCancellationDetailsDTO underlyingCancellationDetails;

    @JsonProperty("settlement_method")
    private String settlementMethod;

    @JsonProperty("clearing_system")
    private String clearingSystem;

    private String from;

    private String to;

    @JsonProperty("sender_acknowledgement_receipt")
    private String senderAcknowledgementReceipt;

    @JsonProperty("received_date")
    private String receivedDate;

    @JsonProperty("instructed_amount")
    private ESBDepositCAGPIStatusAmountDTO instructedAmount;

    @JsonProperty("confirmed_amount")
    private ESBDepositCAGPIStatusAmountDTO confirmedAmount;

    @JsonProperty("interbank_settlement_amount")
    private ESBDepositCAGPIStatusAmountDTO interbankSettlementAmount;

    @JsonProperty("interbank_settlement_date")
    private String interbankSettlementDate;

    @JsonProperty("charge_bearer")
    private String chargeBearer;

    @JsonProperty("charge_amount")
    private List<ESBDepositCAGPIStatusAmountDTO> chargeAmount;

    @JsonProperty("foreign_exchange_details")
    private ESBDepositCAGPIStatusForeignExchangeDetailDTO foreignExchangeDetails;

    @JsonProperty("update_payment")
    private String updatePayment;

    @JsonProperty("duplicate_message_reference")
    private String duplicateMessageReference;

    @JsonProperty("copied_business_service")
    private String copiedBusinessService;

    @JsonProperty("last_update_time")
    private String lastUpdateTime;

    @JsonProperty("TransactionStatusDate")
    private String transactionStatusDate;

    @JsonProperty("AmountInconsistency")
    private String amountInconsistency;

    @JsonProperty("EndToEndIdentification")
    private String endToEndIdentification;

    @JsonProperty("TrackerInformingParty")
    private String trackerInformingParty;

    @JsonProperty("InstructedAgent")
    private String instructedAgent;

    @JsonProperty("ChargesInformation")
    private ESBDepositCAGPIStatusChargesInformationDTO chargesInformation;

    @JsonProperty("ConfirmedDate")
    private String confirmedDate;

    @JsonProperty("RemainingToBeConfirmedAmount")
    private String remainingToBeConfirmedAmount;

    @JsonProperty("PreviouslyConfirmedAmount")
    private String previouslyConfirmedAmount;

    @JsonProperty("InstructingReimbursementAgent")
    private String instructingReimbursementAgent;

    @JsonProperty("PreviousInstructingAgent1")
    private String previousInstructingAgent1;

    @JsonProperty("PreviousInstructingAgent2")
    private String previousInstructingAgent2;

    @JsonProperty("PreviousInstructingAgent3")
    private String previousInstructingAgent3;

    @JsonProperty("IntermediaryAgent1")
    private String intermediaryAgent1;

    @JsonProperty("IntermediaryAgent2")
    private String intermediaryAgent2;

    @JsonProperty("IntermediaryAgent3")
    private String intermediaryAgent3;

    @JsonProperty("Debtor")
    private String debtor;

    @JsonProperty("DebtorAgent")
    private String debtorAgent;

    @JsonProperty("CreditorAgent")
    private String creditorAgent;

    @JsonProperty("Creditor")
    private String creditor;

    @JsonProperty("CreditorAccount")
    private ESBDepositCAGPIStatusCreditorAccountDTO creditorAccount;

    @JsonProperty("RejectReturnReason")
    private String rejectReturnReason;
}
