package org.hyperledger.fabric.bank.payments.utility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Created by Arpit Vaish on 10/18/2019.
 */
public final class PaymentsUtility {
    private static Log logger = LogFactory.getLog(PaymentsUtility.class);

    private PaymentsUtility() {
    }
    public static boolean validateCurrency(final List<String> validCurrency, final String inputCurrency) {
        return validCurrency.contains(inputCurrency.toUpperCase())
                || validCurrency.contains(inputCurrency.toLowerCase());
    }


}
