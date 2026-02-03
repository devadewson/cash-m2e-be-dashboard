package com.m2e.dashboard.mapper.deposit.ca;

import com.m2e.dashboard.dto.common.ContentDTO;
import com.m2e.dashboard.dto.deposit.ca.response.DepositCAGPIBankDTO;
import com.m2e.dashboard.dto.deposit.ca.response.DepositCAGPIStatusDTO;
import com.m2e.dashboard.jms.deposit.ca.response.gpistatus.ESBDepositCAGPIStatusPaymentTrxnDetailsDTO;
import org.mapstruct.Mapper;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ESBDepositCAGPIMapper {
    default DepositCAGPIStatusDTO mapToDepositCAGPIStatusRes(ESBDepositCAGPIStatusPaymentTrxnDetailsDTO response) {
        DepositCAGPIStatusDTO depositCAGPIStatusDTO = new DepositCAGPIStatusDTO();
        depositCAGPIStatusDTO.setBankList(new ArrayList<>());

        // Originating DepositCAGPIBankDTO
        DepositCAGPIBankDTO originatingDepositCAGPIBankDTO = createOriginatingDepositCAGPIBankDTO(response);
        depositCAGPIStatusDTO.getBankList().add(originatingDepositCAGPIBankDTO);

        // Explicitly create intermediary banks with null checks
        if (response.getPaymentEvent() != null && !response.getPaymentEvent().isEmpty()) {
            depositCAGPIStatusDTO.getBankList().add(createIntermediaryDepositCAGPIBankDTO("intermediaryBank1", response.getPaymentEvent().get(0).getIntermediaryAgent1(), response));
            depositCAGPIStatusDTO.getBankList().add(createIntermediaryDepositCAGPIBankDTO("intermediaryBank2", response.getPaymentEvent().get(0).getIntermediaryAgent2(), response));
            depositCAGPIStatusDTO.getBankList().add(createIntermediaryDepositCAGPIBankDTO("intermediaryBank3", response.getPaymentEvent().get(0).getIntermediaryAgent3(), response));
        }
        // Recipient DepositCAGPIBankDTO
        DepositCAGPIBankDTO recipientDepositCAGPIBankDTO = createRecipientDepositCAGPIBankDTO(response);
        depositCAGPIStatusDTO.getBankList().add(recipientDepositCAGPIBankDTO);

        return depositCAGPIStatusDTO;
    }

    default DepositCAGPIBankDTO createOriginatingDepositCAGPIBankDTO(ESBDepositCAGPIStatusPaymentTrxnDetailsDTO response) {
        DepositCAGPIBankDTO depositCAGPIBankRes = new DepositCAGPIBankDTO();
        depositCAGPIBankRes.setBank("originatingBank");
        List<ContentDTO> contentDTOs = new ArrayList<>();
        contentDTOs.add(new ContentDTO("remitterBank", response.getPaymentEvent() != null && !response.getPaymentEvent().isEmpty()
                ? response.getPaymentEvent().get(0).getDebtorAgent()
                : ""));
        contentDTOs.add(new ContentDTO("acceptanceDateTime", response.getInitiationTime()));
        contentDTOs.add(new ContentDTO("elapsedTime", calculateElapsedTime(response.getInitiationTime(), response.getCompletionTime())));
        contentDTOs.add(new ContentDTO("amountRemitted", response.getPaymentEvent() != null && !response.getPaymentEvent().isEmpty()
                ? response.getPaymentEvent().get(0).getInstructedAmount().getCurrency() + " " + response.getPaymentEvent().get(0).getInstructedAmount().getAmount()
                : "N/A"));
        contentDTOs.add(new ContentDTO("charges", response.getPaymentEvent() != null && !response.getPaymentEvent().isEmpty()
                ? response.getPaymentEvent().get(0).getChargeAmount().get(0).getCurrency() + " " + response.getPaymentEvent().get(0).getChargeAmount().get(0).getAmount()
                : "0"));
        contentDTOs.add(new ContentDTO("exchangeRate", response.getPaymentEvent() != null && !response.getPaymentEvent().isEmpty()
                ? response.getPaymentEvent().get(0).getForeignExchangeDetails().getExchangeRate()
                : "N/A")); depositCAGPIBankRes.setContent(contentDTOs);
        return depositCAGPIBankRes;
    }

    default DepositCAGPIBankDTO createIntermediaryDepositCAGPIBankDTO(String depositCAGPIBankResName, String intermediaryAgent, ESBDepositCAGPIStatusPaymentTrxnDetailsDTO response) {
        DepositCAGPIBankDTO depositCAGPIBankRes = new DepositCAGPIBankDTO();
        depositCAGPIBankRes.setBank(depositCAGPIBankResName);
        List<ContentDTO> contentDTOs = new ArrayList<>();
        contentDTOs.add(new ContentDTO("intermediaryBank", intermediaryAgent)); // Dynamic based on DepositCAGPIBankDTO name
        contentDTOs.add(new ContentDTO("acceptanceDateTime", response.getInitiationTime()));
        contentDTOs.add(new ContentDTO("releaseDateTime", response.getCompletionTime()));
        contentDTOs.add(new ContentDTO("elapsedTime", calculateElapsedTime(response.getInitiationTime(), response.getCompletionTime())));
        depositCAGPIBankRes.setContent(contentDTOs);
        return depositCAGPIBankRes;
    }

    default DepositCAGPIBankDTO createRecipientDepositCAGPIBankDTO(ESBDepositCAGPIStatusPaymentTrxnDetailsDTO response) {
        DepositCAGPIBankDTO depositCAGPIBankRes = new DepositCAGPIBankDTO();
        depositCAGPIBankRes.setBank("recipientBank");
        List<ContentDTO> contentDTOs = new ArrayList<>();
        contentDTOs.add(new ContentDTO("recipientBank", response.getPaymentEvent() != null && !response.getPaymentEvent().isEmpty()
                ? response.getPaymentEvent().get(0).getCreditorAgent()
                : ""));
        contentDTOs.add(new ContentDTO("acceptanceDateTime", response.getInitiationTime()));
        contentDTOs.add(new ContentDTO("elapsedTime", calculateElapsedTime(response.getInitiationTime(), response.getCompletionTime())));
        contentDTOs.add(new ContentDTO("amountCredited", response.getPaymentEvent() != null && !response.getPaymentEvent().isEmpty()
                ? response.getPaymentEvent().get(0).getConfirmedAmount().getCurrency() + " " + response.getPaymentEvent().get(0).getConfirmedAmount().getAmount()
                : "N/A"));
        contentDTOs.add(new ContentDTO("charges", response.getPaymentEvent() != null && !response.getPaymentEvent().isEmpty()
                ? response.getPaymentEvent().get(0).getChargeAmount().get(0).getCurrency() + " " + response.getPaymentEvent().get(0).getChargeAmount().get(0).getAmount()
                : "0"));
        contentDTOs.add(new ContentDTO("exchangeRate", response.getPaymentEvent() != null && !response.getPaymentEvent().isEmpty()
                ? response.getPaymentEvent().get(0).getForeignExchangeDetails().getExchangeRate()
                : "N/A"));
        depositCAGPIBankRes.setContent(contentDTOs);
        return depositCAGPIBankRes;
    }

    private String calculateElapsedTime(String start, String end) {
        // Use Optional to handle null or empty values
        String defaultValue = "00 Days 00:00:00";

        Instant startTime = Optional.ofNullable(start)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try {
                        return Instant.parse(s);
                    } catch (DateTimeParseException e) {
                        return null; // Handle parse failure
                    }
                }).orElse(null);

        Instant endTime = Optional.ofNullable(end)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try {
                        return Instant.parse(s);
                    } catch (DateTimeParseException e) {
                        return null; // Handle parse failure
                    }
                }).orElse(null);

        // If either startTime or endTime is null, return the default value
        if (startTime == null || endTime == null) {
            return defaultValue;
        }

        Duration duration = Duration.between(startTime, endTime);

        // If the duration is negative, set it to zero
        duration = duration.isNegative() ? Duration.ZERO : duration;

        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        String formattedDuration = String.format("%02d Days %02d:%02d:%02d", days, hours, minutes, seconds);
        return formattedDuration;
    }
}
