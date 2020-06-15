package org.hyperledger.fabric.bank.payments.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by 1604993 on 11/6/2019.
 */
public class TransactTest {
    @Test
    public void toStringIdentifiestransact() {
        TransactionData transact = new TransactionData("transaction", "TRANS1", "Jon Doe", "INR", "1000",
                "2000", "Jane Doe", "SAVING", "20-01-2020", "INR23423ADF", "SGD");

        assertThat(transact.toString()).isEqualTo("TransactionData{activity='transaction', transactionid='TRANS1', "
                + "userName='Jon Doe', ccy1='INR', amount1='1000', ccamountcounterpartyamount2='2000', "
                + "beneficialryname='Jane Doe', benefiaryaccount='SAVING', transactiondate='20-01-2020', "
                + "cpbic='INR23423ADF', ccy2='SGD'}");
    }

    @Nested
    class Equality {

        @Test
        public void isReflexive() {
            TransactionData transact = new TransactionData("transaction", "TRANS1", "Jon Doe", "INR", "1000",
                    "2000", "Jane Doe", "SAVING", "20-01-2020", "INR23423ADF", "SGD");
            assertThat(transact).isEqualTo(transact);
        }

        @Test
        public void isSymmetric() {
            TransactionData transactA = new TransactionData("transaction", "TRANS1", "Jon Doe", "INR", "1000",
                    "2000", "Jane Doe", "SAVING", "20-01-2020", "INR23423ADF", "SGD");
            TransactionData transactB = new TransactionData("transaction", "TRANS1", "Jon Doe", "INR", "1000",
                    "2000", "Jane Doe", "SAVING", "20-01-2020", "INR23423ADF", "SGD");

            assertThat(transactA).isEqualTo(transactB);
            assertThat(transactB).isEqualTo(transactA);
        }

        @Test
        public void isTransitive() {
            TransactionData transactA = new TransactionData("transaction", "TRANS1", "Jon Doe", "INR", "1000",
                    "2000", "Jane Doe", "SAVING", "20-01-2020", "INR23423ADF", "SGD");
            TransactionData transactB = new TransactionData("transaction", "TRANS1", "Jon Doe", "INR", "1000",
                    "2000", "Jane Doe", "SAVING", "20-01-2020", "INR23423ADF", "SGD");
            TransactionData transactC = new TransactionData("transaction", "TRANS1", "Jon Doe", "INR", "1000",
                    "2000", "Jane Doe", "SAVING", "20-01-2020", "INR23423ADF", "SGD");

            assertThat(transactA).isEqualTo(transactB);
            assertThat(transactB).isEqualTo(transactC);
            assertThat(transactA).isEqualTo(transactC);
        }

        @Test
        public void handlesInequality() {
            TransactionData transactA = new TransactionData("transaction", "TRANS1", "Jon Doe", "INR", "1000",
                    "2000", "Jane Doe", "SAVING", "20-01-2020", "INR23423ADF", "SGD");
            TransactionData transactB = new TransactionData("activity", "TRANS1", "Jon Doe", "INR", "1000",
                    "2001", "Jane Doe", "SAVING", "20-01-2020", "INR23423ADF", "SGD");

            assertThat(transactA).isNotEqualTo(transactB);
        }

        @Test
        public void handlesOtherObjects() {
            TransactionData transactA = new TransactionData("transaction", "TRANS1", "Jon Doe", "INR", "1000",
                    "2000", "Jane Doe", "SAVING", "20-01-2020", "INR23423ADF", "SGD");
            String transactB = "not a transact";

            assertThat(transactA).isNotEqualTo(transactB);
        }

        @Test
        public void handlesNull() {
            TransactionData transact = new TransactionData("transaction", "TRANS1", "Jon Doe", "INR", "1000",
                    "2000", "Jane Doe", "SAVING", "20-01-2020", "INR23423ADF", "SGD");

            assertThat(transact).isNotEqualTo(null);
        }
    }
}
