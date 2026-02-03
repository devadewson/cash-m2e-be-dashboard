package com.m2e.dashboard.jms.einvoice.request.tupdateParty;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MsgBody{

	@JsonProperty("transactionType")
	private String transactionType;

	@JsonProperty("crsList")
	private List<CrsListItem> crsList;

	@JsonProperty("additionalDetails")
	private AdditionalDetails additionalDetails;

	@JsonProperty("type")
	private String type;

	@JsonProperty("onboardingInformation")
	private OnboardingInformation onboardingInformation;

	@JsonProperty("ucif")
	private Ucif ucif;

	@JsonProperty("prefferedDateFormat")
	private String prefferedDateFormat;

	@JsonProperty("requestor")
	private String requestor;

	@JsonProperty("localCIF")
	private LocalCIF localCIF;

	@JsonProperty("demographics")
	private Demographics demographics;

	@JsonProperty("countryOfOnboarding")
	private String countryOfOnboarding;
}