package com.m2e.dashboard.mapper.overview;

import com.m2e.dashboard.dto.overview.response.AssetLiabilitiesResDTO;
import com.m2e.dashboard.dto.overview.response.OverviewSubResDTO;
import com.m2e.dashboard.enums.OvSubProductEnum;
import com.m2e.dashboard.sp.overview.response.OverviewSpDTO;
import com.m2e.dashboard.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class OverviewMapper {
        public static AssetLiabilitiesResDTO mapToAssetLiabilities(List<OverviewSubResDTO> overviewList, AssetLiabilitiesResDTO assetLiabilitiesResDto) {

            for (OverviewSubResDTO dto : overviewList) {
                BigDecimal amount = dto.getTotalAmount() != null ? dto.getTotalAmount() : BigDecimal.ZERO;
                log.info("Amount "+ dto.getSubProduct() + " " + amount);
                // Set common values (assuming same for all)
                //assetLiabilitiesResDto.setCurrency(dto.getCurrency());
                assetLiabilitiesResDto.setLastUpdatedDate(dto.getAsOf()!=null?dto.getAsOf():DateTimeUtil.getTodayDate());

                switch (dto.getSubProduct()) {
                    case CA:
                        assetLiabilitiesResDto.setTotalCA(amount);
                        break;
                    case MFCA:
                        assetLiabilitiesResDto.setTotalMFCA(amount);
                        break;
                    case FCA:
                        assetLiabilitiesResDto.setTotalFCA(amount);
                        break;
                    case FD:
                        assetLiabilitiesResDto.setTotalFD(amount);
                        break;
                    case TD:
                        assetLiabilitiesResDto.setTotalTD(amount);
                        break;
                    case VA:
                        assetLiabilitiesResDto.setTotalVA(amount);
                        break;
                    case DEPOSIT:
                        assetLiabilitiesResDto.setTotalDpOther(amount);
                        break;
                    case UT:
                        assetLiabilitiesResDto.setTotalInv(amount);
                        break;
                    case LOAN:
                        assetLiabilitiesResDto.setTotalLoanOther(amount);
                        break;
                    case OD:
                        assetLiabilitiesResDto.setTotalOD(amount);
                        break;
                    case CRC:
                        assetLiabilitiesResDto.setTotalCrc(amount);
                        break;
                    case CHC:
                        assetLiabilitiesResDto.setTotalChc(amount);
                        break;
                    case DC:
                        assetLiabilitiesResDto.setTotalDc(amount);
                        break;
                    case TRD:
                        assetLiabilitiesResDto.setTotalTRD(amount);
                        break;
                    case E:
                        assetLiabilitiesResDto.setTotalTRDE(amount);
                        break;
                    case I:
                        assetLiabilitiesResDto.setTotalTRDI(amount);
                        break;
                    case TL:
                        assetLiabilitiesResDto.setTotalTL(amount);
                        break;
                    case RC:
                        assetLiabilitiesResDto.setTotalRc(amount);
                        break;
                    case HP:
                        assetLiabilitiesResDto.setTotalHp(amount);
                        break;
                    case BHP:
                        assetLiabilitiesResDto.setTotalBhp(amount);
                        break;
                    case FS:
                        assetLiabilitiesResDto.setTotalFs(amount);
                        break;
                    case BD:
                        assetLiabilitiesResDto.setTotalBD(amount);
                        break;
                    case PBR:
                        assetLiabilitiesResDto.setTotalPbr(amount);
                        break;
                    default:
                        throw new IllegalArgumentException("No Sub Product Defined");
                }
            }
            return assetLiabilitiesResDto;
        }

    public static OverviewSpDTO mapToSpDto(OverviewSubResDTO source) {
        OverviewSpDTO target = new OverviewSpDTO();

        target.setSubProductDesc(source.getSubProductDesc());
        target.setNoOfAccounts(source.getTotNoOfAccounts());

        switch (source.getSubProduct()) {

        //for assets
            case SA:
            case CA:
                target.setLedgerBalance(source.getTotalAmount());
                target.setAvailableBalance(source.getTotalAmount());
                target.setOutstandingBalance(BigDecimal.ZERO);
                target.setFacilityLimit(BigDecimal.ZERO);
                break;
            case FCA:
            case MFCA:
            case FD:
            case TD:
            case UT:
            case DEPOSIT:
                target.setLedgerBalance(source.getTotalAmount());
                target.setAvailableBalance(source.getTotalAmount());
                target.setOutstandingBalance(BigDecimal.ZERO);
                target.setFacilityLimit(BigDecimal.ZERO);
                break;

        //for liabilities
            case OD:
            case RC:
            case TL:
            case TRD:
            case I:
            case E:
            case HP:
            case BHP:
                target.setLedgerBalance(BigDecimal.ZERO);
                target.setAvailableBalance(BigDecimal.ZERO);
                target.setOutstandingBalance(source.getTotalAmount());
                target.setFacilityLimit(source.getFacilityLimit());
                break;

            default:
                target.setOutstandingBalance(BigDecimal.ZERO);
                target.setFacilityLimit(BigDecimal.ZERO);
                target.setLedgerBalance(BigDecimal.ZERO);
                target.setAvailableBalance(BigDecimal.ZERO);
                break;
        }

        return target;
    }

}

