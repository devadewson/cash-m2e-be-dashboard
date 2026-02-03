package com.m2e.dashboard.mapper.einvoice;

import com.m2e.dashboard.dto.einvoice.response.EInvoiceCorpDetailDTO;
import com.m2e.dashboard.jms.einvoice.response.tviewParty.ESBTViewPartyFullMsgBodyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EInvoiceDetailsMapper {

    @Mapping(target = "tin", expression = "java(msgBody.getCrsList() != null && !msgBody.getCrsList().isEmpty() ? msgBody.getCrsList().get(0).getTaxId() : null)")
    @Mapping(target = "stateProvince", expression = "java(msgBody.getAddresses() != null && !msgBody.getAddresses().isEmpty() && msgBody.getAddresses().get(0).getAddress() != null && msgBody.getAddresses().get(0).getAddress().getState() != null ? msgBody.getAddresses().get(0).getAddress().getState().getValue() : null)")
    @Mapping(target = "sst", expression = "java(msgBody.getAdditionalDetails() != null ? msgBody.getAdditionalDetails().getSalesServicesTaxNo() : null)")
    @Mapping(target = "prefecture", expression = "java(msgBody.getAddresses() != null && !msgBody.getAddresses().isEmpty() && msgBody.getAddresses().get(0).getAddress() != null ? msgBody.getAddresses().get(0).getAddress().getTown() : null)")
    @Mapping(target = "postcode", expression = "java(msgBody.getAddresses() != null && !msgBody.getAddresses().isEmpty() && msgBody.getAddresses().get(0).getAddress() != null ? msgBody.getAddresses().get(0).getAddress().getPostalCode() : null)")
    @Mapping(target = "emailAddress", expression = "java(msgBody.getCorporateContacts() != null && !msgBody.getCorporateContacts().isEmpty() ? msgBody.getCorporateContacts().get(0).getEmailAddress() : null)")
    @Mapping(target = "country", expression = "java(msgBody.getAddresses() != null && !msgBody.getAddresses().isEmpty() && msgBody.getAddresses().get(0).getAddress() != null && msgBody.getAddresses().get(0).getAddress().getCountry() != null ? msgBody.getAddresses().get(0).getAddress().getCountry().getValue() : null)")
    @Mapping(target = "corpName", expression = "java(msgBody.getDemographics() != null && msgBody.getDemographics().getPartyName() != null ? msgBody.getDemographics().getPartyName().getRegisteredName() : null)")
    @Mapping(target = "cityDistrict", expression = "java(msgBody.getAddresses() != null && !msgBody.getAddresses().isEmpty() && msgBody.getAddresses().get(0).getAddress() != null ? msgBody.getAddresses().get(0).getAddress().getTown() : null)")
    @Mapping(target = "addressLine3", expression = "java(msgBody.getAddresses() != null && !msgBody.getAddresses().isEmpty() && msgBody.getAddresses().get(0).getAddress() != null ? msgBody.getAddresses().get(0).getAddress().getLine3() : null)")
    @Mapping(target = "addressLine2", expression = "java(msgBody.getAddresses() != null && !msgBody.getAddresses().isEmpty() && msgBody.getAddresses().get(0).getAddress() != null ? msgBody.getAddresses().get(0).getAddress().getLine2() : null)")
    @Mapping(target = "addressLine1", expression = "java(msgBody.getAddresses() != null && !msgBody.getAddresses().isEmpty() && msgBody.getAddresses().get(0).getAddress() != null ? msgBody.getAddresses().get(0).getAddress().getLine1() : null)")
    @Mapping(target = "contactNumber", expression = "java(msgBody.getCorporateContacts() != null && !msgBody.getCorporateContacts().isEmpty() && msgBody.getCorporateContacts().get(0).getPhone() != null ? msgBody.getCorporateContacts().get(0).getPhone().getPhoneNumber() : null)")
    EInvoiceCorpDetailDTO mapHostToDetails(ESBTViewPartyFullMsgBodyDTO msgBody);

}

