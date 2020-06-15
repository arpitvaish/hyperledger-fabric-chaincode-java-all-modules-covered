package org.hyperledger.fabric.scb.payments.utility;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by 1604993 on 11/4/2019.
 */
public class PaymentsUtilityTest {

    @Test
    public void validateCurrency() {
        List<String> validCurrencies = new ArrayList<>();
        validCurrencies.add("SGD");
        validCurrencies.add("USD");
        validCurrencies.add("INR");
        String inputCurrency = "INR";
        assertThat(PaymentsUtility.validateCurrency(validCurrencies, inputCurrency)).isTrue();
    }
}
