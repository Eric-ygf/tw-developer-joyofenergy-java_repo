package uk.tw.energy.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AccountService {

    /** 各个电表所使用的价格计划 */
    private final Map<String, String> smartMeterToPricePlanAccounts;

    public AccountService(Map<String, String> smartMeterToPricePlanAccounts) {
        this.smartMeterToPricePlanAccounts = smartMeterToPricePlanAccounts;
    }

    public String getPricePlanIdForSmartMeterId(String smartMeterId) {
        return smartMeterToPricePlanAccounts.get(smartMeterId);
    }
}
