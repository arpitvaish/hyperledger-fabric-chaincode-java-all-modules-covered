/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.bank.payments.model;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public final class TransactionData {

    @Property()
    private final String activity;

    @Property()
    private final String transactionid;

    @Property()
    private final String userName;

    @Property()
    private final String ccy1;

    @Property()
    private final String amount1;

    @Property()
    private final String ccamountcounterpartyamount2;

    @Property()
    private final String beneficialryname;

    @Property()
    private final String benefiaryaccount;

    @Property()
    private final String transactiondate;

    @Property()
    private final String cpbic;

    @Property()
    private final String ccy2;

    public TransactionData(@JsonProperty("activity") final String activity, @JsonProperty("transactionid") final String transactionid, @JsonProperty("userName") final String
            userName, @JsonProperty("ccy1") final String ccy1, @JsonProperty("amount1") final String
                                   amount1, @JsonProperty("ccamountcounterpartyamount2") final String
                                   ccamountcounterpartyamount2, @JsonProperty("beneficialryname") final String beneficialryname,
                           @JsonProperty("benefiaryaccount") final String benefiaryaccount,
                           @JsonProperty("transactiondate") final String transactiondate,
                           @JsonProperty("cpbic") final String cpbic, @JsonProperty("ccy2") final String
                                   ccy2) {
        this.activity = activity;
        this.transactionid = transactionid;
        this.userName = userName;
        this.ccy1 = ccy1;
        this.amount1 = amount1;
        this.ccamountcounterpartyamount2 = ccamountcounterpartyamount2;
        this.beneficialryname = beneficialryname;
        this.benefiaryaccount = benefiaryaccount;
        this.transactiondate = transactiondate;
        this.cpbic = cpbic;
        this.ccy2 = ccy2;
    }

    public String getActivity() {
        return activity;
    }

    public String getTransactionid() {
        return transactionid;
    }

    public String getUserName() {
        return userName;
    }

    public String getCcy1() {
        return ccy1;
    }

    public String getAmount1() {
        return amount1;
    }

    public String getCcamountcounterpartyamount2() {
        return ccamountcounterpartyamount2;
    }

    public String getBeneficialryname() {
        return beneficialryname;
    }

    public String getBenefiaryaccount() {
        return benefiaryaccount;
    }

    public String getTransactiondate() {
        return transactiondate;
    }

    public String getCpbic() {
        return cpbic;
    }

    public String getCcy2() {
        return ccy2;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        TransactionData other = (TransactionData) obj;

        return Objects.deepEquals(new String[] {getActivity(), getTransactionid(), getUserName(),
                        getCcy1(), getAmount1()},
                new String[] {other.getActivity(), other.getTransactionid(), other.getUserName(),
                        other.getCcy1(), other.getAmount1()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getActivity(), getTransactionid(), getUserName(), getCcy1(), getAmount1());
    }

    @Override
    public String toString() {
        return "TransactionData{"
                + "activity='" + activity + '\''
                + ", transactionid='" + transactionid + '\''
                + ", userName='" + userName + '\''
                + ", ccy1='" + ccy1 + '\''
                + ", amount1='" + amount1 + '\''
                + ", ccamountcounterpartyamount2='" + ccamountcounterpartyamount2 + '\''
                + ", beneficialryname='" + beneficialryname + '\''
                + ", benefiaryaccount='" + benefiaryaccount + '\''
                + ", transactiondate='" + transactiondate + '\''
                + ", cpbic='" + cpbic + '\''
                + ", ccy2='" + ccy2 + '\''
                + '}';
    }
}
