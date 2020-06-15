package org.hyperledger.fabric.scb.payments.model;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

/**
 * Created by 1604993 on 11/7/2019.
 */
@DataType()
public final class PaymentTransaction {
    @Property()
    private TransactionData transactionData;
    @Property()
    private TransactionPrivateData transactionPrivateData;

    public PaymentTransaction(@JsonProperty("transactionData") final TransactionData transactionData,
                              @JsonProperty("transactionPrivateData") final TransactionPrivateData
                                      transactionPrivateData) {
        this.transactionData = transactionData;
        this.transactionPrivateData = transactionPrivateData;
    }

    public TransactionData getTransactionData() {
        return transactionData;
    }

    public void setTransactionData(final TransactionData transactionData) {
        this.transactionData = transactionData;
    }

    public TransactionPrivateData getTransactionPrivateData() {
        return transactionPrivateData;
    }

    public void setTransactionPrivateData(final TransactionPrivateData transactionPrivateData) {
        this.transactionPrivateData = transactionPrivateData;
    }

    @Override
    public String toString() {
        return "PaymentTransaction{"
                + "transactionData=" + transactionData
                + ", transactionPrivateData=" + transactionPrivateData
                + '}';
    }
}
