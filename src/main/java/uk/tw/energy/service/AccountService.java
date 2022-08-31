package uk.tw.energy.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AccountService {

    /**
     * 各台电表的计价方案，类似于存储在db中
     */
    private final Map<String, String> smartMeterToPricePlanAccounts;//SeedingApplicationDataConfiguration.smartMeterToPricePlanAccounts生成

    public AccountService(Map<String, String> smartMeterToPricePlanAccounts) {
        this.smartMeterToPricePlanAccounts = smartMeterToPricePlanAccounts;
    }

    /**
     * rt
     */
    public String getPricePlanIdForSmartMeterId(String smartMeterId) {
        return smartMeterToPricePlanAccounts.get(smartMeterId);
    }
}
