package org.hyperledger.fabric.scb.payments.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by 1604993 on 11/6/2019.
 */
public class TransactionPrivateDataTest {
    @Test
    public void toStringIdentifiestransact() {
        TransactionPrivateData transact = new TransactionPrivateData("TRANS1", "25");

        assertThat(transact.toString()).isEqualTo("TransactionPrivateData [transactionId=TRANS1, rate=25]");
    }

    @Nested
    class Equality {

        @Test
        public void isReflexive() {
            TransactionPrivateData transact = new TransactionPrivateData("TRANS1", "25");
            assertThat(transact).isEqualTo(transact);
        }

        @Test
        public void isSymmetric() {
            TransactionPrivateData transactA = new TransactionPrivateData("TRANS1", "25");
            TransactionPrivateData transactB = new TransactionPrivateData("TRANS1", "25");

            assertThat(transactA).isEqualTo(transactB);
            assertThat(transactB).isEqualTo(transactA);
        }

        @Test
        public void isTransitive() {
            TransactionPrivateData transactA = new TransactionPrivateData("TRANS1", "25");
            TransactionPrivateData transactB = new TransactionPrivateData("TRANS1", "25");
            TransactionPrivateData transactC = new TransactionPrivateData("TRANS1", "25");

            assertThat(transactA).isEqualTo(transactB);
            assertThat(transactB).isEqualTo(transactC);
            assertThat(transactA).isEqualTo(transactC);
        }

        @Test
        public void handlesInequality() {
            TransactionPrivateData transactA = new TransactionPrivateData("TRANS1", "25");
            TransactionPrivateData transactB = new TransactionPrivateData("TRANS2", "27");

            assertThat(transactA).isNotEqualTo(transactB);
        }

        @Test
        public void handlesOtherObjects() {
            TransactionPrivateData transactA = new TransactionPrivateData("TRANS1", "25");
            String transactB = "not a transact";
            assertThat(transactA).isNotEqualTo(transactB);
        }

        @Test
        public void handlesNull() {
            TransactionPrivateData transact = new TransactionPrivateData("TRANS1", "25");
            assertThat(transact).isNotEqualTo(null);
        }
    }
}
