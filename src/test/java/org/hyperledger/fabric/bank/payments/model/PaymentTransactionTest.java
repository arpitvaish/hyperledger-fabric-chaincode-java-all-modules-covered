package org.hyperledger.fabric.bank.payments.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by 1604993 on 11/12/2019.
 */
public class PaymentTransactionTest {

    static TransactionData transactionData;
    static TransactionData transactionDataB;
    static TransactionPrivateData transactionPrivateData;
    static TransactionPrivateData transactionPrivateDataB;


    @BeforeAll
    public static void setUp() {
        transactionData = new TransactionData("transaction", "TRANS1", "Jon Doe", "INR", "1000",
                "2000", "Jane Doe", "SAVING", "20-01-2020", "INR23423ADF", "SGD");
        transactionDataB = new TransactionData("transaction", "TRANS2", "Jon Doe", "INR", "1000",
                "2000", "Jane Doe", "SAVING", "20-01-2020", "INR23423ADF", "SGD");
        transactionPrivateData = new TransactionPrivateData("TRANS1", "25");
        transactionPrivateDataB = new TransactionPrivateData("TRANS2", "25");
    }

    @Test
    public void toStringIdentifiestransact() {
        PaymentTransaction paymentTransaction = new PaymentTransaction(transactionData, transactionPrivateData);
        assertThat(paymentTransaction.toString()).isEqualTo("PaymentTransaction{transactionData=TransactionData{activity='transaction', "
                + "transactionid='TRANS1', userName='Jon Doe', ccy1='INR', amount1='1000', ccamountcounterpartyamount2='2000', "
                + "beneficialryname='Jane Doe', benefiaryaccount='SAVING', transactiondate='20-01-2020', cpbic='INR23423ADF', ccy2='SGD'}, "
                + "transactionPrivateData=TransactionPrivateData [transactionId=TRANS1, rate=25]}");
    }

    @Nested
    class Equality {

        @Test
        public void isReflexive() {
            PaymentTransaction paymentTransaction = new PaymentTransaction(transactionData, transactionPrivateData);
            assertThat(paymentTransaction).isEqualTo(paymentTransaction);
        }

        /*@Test
        public void isSymmetric() {
            PaymentTransaction paymentTransaction1 = new PaymentTransaction(transactionData, transactionPrivateData);
            PaymentTransaction paymentTransaction2 = new PaymentTransaction(transactionData, transactionPrivateData);

            assertThat(paymentTransaction1).isEqualTo(paymentTransaction2);
            assertThat(paymentTransaction2).isEqualTo(paymentTransaction1);
        }

        @Test
        public void isTransitive() {
            PaymentTransaction paymentTransactionA = new PaymentTransaction(transactionData, transactionPrivateData);
            PaymentTransaction paymentTransactionB = new PaymentTransaction(transactionData, transactionPrivateData);
            PaymentTransaction paymentTransactionC = new PaymentTransaction(transactionData, transactionPrivateData);

            assertThat(paymentTransactionA).isEqualTo(paymentTransactionB);
            assertThat(paymentTransactionB).isEqualTo(paymentTransactionC);
            assertThat(paymentTransactionA).isEqualTo(paymentTransactionC);
        }*/

        @Test
        public void handlesInequality() {
            PaymentTransaction paymentTransactionA = new PaymentTransaction(transactionData, transactionPrivateData);
            PaymentTransaction paymentTransactionB = new PaymentTransaction(transactionDataB, transactionPrivateDataB);

            assertThat(paymentTransactionA).isNotEqualTo(paymentTransactionB);
        }

        @Test
        public void handlesOtherObjects() {
            PaymentTransaction paymentTransactionA = new PaymentTransaction(transactionData, transactionPrivateData);
            String transactB = "not a transaction";

            assertThat(paymentTransactionA).isNotEqualTo(transactB);
        }

        @Test
        public void handlesNull() {
            PaymentTransaction paymentTransactionA = new PaymentTransaction(transactionData, transactionPrivateData);
            assertThat(paymentTransactionA).isNotEqualTo(null);
        }
    }
}
