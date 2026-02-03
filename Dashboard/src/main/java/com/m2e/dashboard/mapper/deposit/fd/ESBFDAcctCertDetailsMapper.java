package com.m2e.dashboard.mapper.deposit.fd;

import com.m2e.dashboard.dto.deposit.fd.response.DepositFDCertificateDetailDTO;
import com.m2e.dashboard.jms.common.response.ESBDetailDTO;
import com.m2e.dashboard.jms.common.response.ESBDetailDTOAware;
import com.m2e.dashboard.jms.common.response.ESBDetailDTOHolder;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ESBFDAcctCertDetailsMapper {

    @Mapping(target = "certificateNumber", expression = "java(mapCertDetails(dto.getDetails(), \"certificateNo\"))")
    @Mapping(target = "certStatusCode", expression = "java(mapCertDetails(dto.getDetails(), \"certificateStatus\"))")
    @Mapping(target = "principalAmount", expression = "java(mapCertDetails(dto.getDetails(), \"originalAmount\"))")
    @Mapping(target = "placementDate", expression = "java(mapCertDetails(dto.getDetails(), \"placementDate\"))")
    @Mapping(target = "interestRate", expression = "java(mapCertDetails(dto.getDetails(), \"interestRate\"))")
    @Mapping(target = "tenure", expression = "java(mapCertDetails(dto.getDetails(), \"period\"))")
    @Mapping(target = "periodTerm", expression = "java(mapCertDetails(dto.getDetails(), \"periodTerm\"))")
    @Mapping(target = "maturityInterest", expression = "java(mapCertDetails(dto.getDetails(), \"maturityInterest\"))")
    @Mapping(target = "maturityDate", expression = "java(mapCertDetails(dto.getDetails(), \"maturityDate\"))")
    @Mapping(target = "instructionOnMaturity", expression = "java(mapCertDetails(dto.getDetails(), \"instructionMaturity\"))")
    @Mapping(target = "interestPaymentMode", expression = "java(mapCertDetails(dto.getDetails(), \"interestPayMode\"))")
    @Mapping(target = "interestPaymentFrequency", expression = "java(mapCertDetails(dto.getDetails(), \"PaymentFrequency\"))")
    @Mapping(target = "principalCreditNumber", expression = "java(mapCertDetails(dto.getDetails(), \"PrincipalCreditAccno\"))")
    @Mapping(target = "interestCreditNumber", expression = "java(mapCertDetails(dto.getDetails(), \"InterestCreditAccno\"))")
    @Mapping(target = "prematureClosureIndicator", expression = "java(mapCertDetails(dto.getDetails(), \"PrematureClosureIndicator\"))")
    @Mapping(target = "profitSharingRatio", expression = "java(mapCertDetails(dto.getDetails(), \"ProfitSharingRatio\"))")
    @Mapping(target = "lcrIndicator", expression = "java(mapCertDetails(dto.getDetails(), \"LCRIndicator\"))")
    @Mapping(target = "lcrDate", expression = "java(mapCertDetails(dto.getDetails(), \"LCRDate\"))")
    public abstract DepositFDCertificateDetailDTO.FullData mapToDepositFDCertDetailMYResp(ESBDetailDTOAware dto);

    protected abstract DepositFDCertificateDetailDTO.RawData toRawData(DepositFDCertificateDetailDTO.FullData source);

    @AfterMapping
    protected void fillRawData(@MappingTarget DepositFDCertificateDetailDTO.FullData target) {
        DepositFDCertificateDetailDTO.RawData rawCopy = toRawData(target);
        target.setRawData(rawCopy);
    }

    public DepositFDCertificateDetailDTO.FullData mapToDepositFDCertDetailMYResp(List<ESBDetailDTO> details) {
        return mapToDepositFDCertDetailMYResp(new ESBDetailDTOHolder(details));
    }

    @Mapping(target = "certificateNumber", expression = "java(mapCertDetails(dto.getDetails(), \"certificateNo\"))")
    @Mapping(target = "principalAmount", expression = "java(mapCertDetails(dto.getDetails(), \"originalAmount\"))")
    @Mapping(target = "placementDate", expression = "java(mapCertDetails(dto.getDetails(), \"placementDate\"))")
    @Mapping(target = "interestRate", expression = "java(mapCertDetails(dto.getDetails(), \"interestRate\"))")
    @Mapping(target = "tenure", expression = "java(mapCertDetails(dto.getDetails(), \"period\"))")
    @Mapping(target = "maturityInterest", expression = "java(mapCertDetails(dto.getDetails(), \"maturityInterest\"))")
    @Mapping(target = "maturityDate", expression = "java(mapCertDetails(dto.getDetails(), \"maturityDate\"))")
    @Mapping(target = "instructionOnMaturity", expression = "java(mapCertDetails(dto.getDetails(), \"instructionMaturity\"))")
    @Mapping(target = "interestPaymentMode", expression = "java(mapCertDetails(dto.getDetails(), \"interestPayMode\"))")
    @Mapping(target = "interestCreditNumber", expression = "java(mapCertDetails(dto.getDetails(), \"interestCreditAccountId\"))")
    @Mapping(target = "upfrontInterestRatePA", expression = "java(mapCertDetails(dto.getDetails(), \"upfrontInterestRate\"))")
    @Mapping(target = "interestOnInterestRatePA", expression = "java(mapCertDetails(dto.getDetails(), \"interestOnInterestRate\"))")
    @Mapping(target = "totalInterest", expression = "java(mapCertDetails(dto.getDetails(), \"totalInterest\"))")
    @Mapping(target = "upfrontInterest", expression = "java(mapCertDetails(dto.getDetails(), \"upfrontInterest\"))")
    @Mapping(target = "interestOnInterestRate", expression = "java(mapCertDetails(dto.getDetails(), \"interestOnInterestRate\"))")
    @Mapping(target = "totalInterestMaturity", expression = "java(mapCertDetails(dto.getDetails(), \"totalInterestUponMaturity\"))")
    public abstract DepositFDCertificateDetailDTO.FullData mapToDepositFDCertDetailSGResp(ESBDetailDTOAware dto);

    protected String mapCertDetails(List<ESBDetailDTO> details, String certDetail) {
        if (details == null || certDetail == null)
            return null;

        return details.stream()
                .filter(detail -> detail != null && detail.getName() != null && certDetail.equalsIgnoreCase(detail.getName()))
                .map(detail -> detail.getValue() != null ? detail.getValue() : "")
                .findFirst()
                .orElse(null);
    }
}
