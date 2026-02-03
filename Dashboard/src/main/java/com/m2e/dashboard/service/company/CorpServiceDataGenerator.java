package com.m2e.dashboard.service.company;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CorpServiceDataGenerator {
    public List<String> generateTransactionCodeList() {
        List<String> trxnCodeList = new ArrayList<>();
        for (int x=0; x<10; x++){
            trxnCodeList.add("TRXN-CODE-"+Integer.toString((x)));
        }
        return trxnCodeList;
    }

    public List<String> generatePaymentTypeList() {
        List<String> trxnCodeList = new ArrayList<>();
        for (int x=0; x<10; x++){
            trxnCodeList.add("PAYMENT-TYPE-"+Integer.toString((x)));
        }
        return trxnCodeList;
    }

    public List<String> generateTransactionTypeList() {
        List<String> trxnCodeList = new ArrayList<>();
        for (int x=0; x<10; x++){
            trxnCodeList.add("TRXN-TYPE-"+Integer.toString((x)));
        }
        return trxnCodeList;
    }
}
