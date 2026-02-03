package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class ConnectedPartiesItem{

	@JsonProperty("relationTypes")
	private List<BaseCode> relationTypes;

	@JsonProperty("identifier")
	private int identifier;

	@JsonProperty("party")
	private Party party;
}