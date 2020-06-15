package org.hyperledger.fabric.bank.payments.model;

/**
 * Created by Arpit Vaish on 10/10/2019.
 */

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public final class TransactionPrivateData {

    @Property()
    private final String transactionid;

    @Property()
    private final String rate;

    public TransactionPrivateData(@JsonProperty("transactionid") final String transactionid,
                                  @JsonProperty("rate") final String
                                          rate) {
        this.transactionid = transactionid;
        this.rate = rate;
    }

    public String getTransactionid() {
        return transactionid;
    }

    public String getRate() {
        return rate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TransactionPrivateData that = (TransactionPrivateData) o;


        return Objects.deepEquals(new String[] {getTransactionid(), getRate()},
                new String[] {that.getTransactionid(), that.getRate()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTransactionid(), getRate());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + " " + "[transactionId=" + transactionid + ", rate=" + rate + "]";
    }
}
