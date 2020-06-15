package org.hyperledger.fabric.scb.payments.manager;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.scb.payments.constants.Constants;
import org.hyperledger.fabric.scb.payments.model.PaymentTransaction;
import org.hyperledger.fabric.scb.payments.model.TransactionData;
import org.hyperledger.fabric.scb.payments.model.TransactionPrivateData;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 1604993 on 11/12/2019.
 */
public class ScbPaymentsTest {


    private static ScbPayments scbPayments;
    private static Genson genson;
    private static Context ctx;
    private static ChaincodeStub stub;

    @BeforeAll
    public static void setUp() {
        scbPayments = new ScbPayments();
        genson = new Genson();
        ctx = mock(Context.class);
        stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);
    }

    @Test
    public void initLedger() {

        Chaincode.Response response = scbPayments.initLedger(ctx);
        assertThat(Integer.parseInt(response.getStringPayload())).isEqualTo(Constants.THREE);
    }

    @Test
    public void queryAllTransaction() {
        when(stub.getStateByRange("TRANS0", "TRANS999")).thenReturn(new MockPaymentResultsIterator());
        TransactionData[] transactions = genson.deserialize(scbPayments.queryAllTransaction(ctx).getPayload(), TransactionData[].class);
        final List<TransactionData> expectedPayments = new ArrayList<>();
        expectedPayments.add(new TransactionData("transaction", "TRANS000", "Jon Doe", "INR", "1000",
                "2000", "Jane Doe", "SAVING", "20-01-2020", "INR23423ADF", "SGD"));
        expectedPayments.add(new TransactionData("transaction", "TRANS001", "Jon Doe", "INR", "1000",
                "2000", "Jane Doe", "SAVING", "20-01-2020", "INR23423ADF", "SGD"));
        assertThat(transactions).containsExactlyElementsOf(expectedPayments);
    }

    @Test
    public void queryAllTransactionWithPrivateData() {
        when(stub.getStateByRange("TRANS0", "TRANS999")).thenReturn(new MockPaymentResultsIterator());
        when(stub.getPrivateDataUTF8("collectionTransactionPrivateData", "TRANS000")).thenReturn("{\"transactionid\": \"TRANS000\",\"rate\": \"25\"}");
        when(stub.getPrivateDataUTF8("collectionTransactionPrivateData", "TRANS001")).thenReturn("{\"transactionid\": \"TRANS001\",\"rate\": \"25\"}");
        PaymentTransaction[] transactions = genson.deserialize(scbPayments.queryAllTransactionwithPrivateData(ctx).getPayload(), PaymentTransaction[].class);
        TransactionPrivateData privateDataA = new TransactionPrivateData("TRANS000", "25");
        TransactionPrivateData privateDataB = new TransactionPrivateData("TRANS001", "25");
        assertThat(transactions).hasSize(2);
    }

    @Test
    public void queryTransactionPrivateDataById() {
        when(stub.getPrivateDataUTF8("collectionTransactionPrivateData", "TRANS000")).thenReturn("{\"transactionid\": \"TRANS000\",\"rate\": \"25\"}");
        when(stub.getParameters()).thenReturn(new ArrayList<String>() {{
            add("TRANS000");
        }});
        TransactionPrivateData privateData = genson.deserialize(scbPayments.queryTransactionPrivateDataById(ctx).getPayload(), TransactionPrivateData.class);
        assertThat(privateData).isNotNull();
    }

    @Test
    public void submitTransaction() {
        when(stub.getParameters()).thenReturn(new ArrayList<String>() {{
            add("transaction");
            add("TRANS000");
            add("Jon Doe");
            add("INR");
            add("1000");
            add("2000");
            add("Jane Doe");
            add("SAVING");
            add("20-01-2020");
            add("INR23423ADF");
            add("SGD");
        }});
        when(stub.getStateValidationParameter("TRANS000")).thenReturn(null);
        Map<String, byte[]> transientDataMap = new HashMap<>();
        TransactionPrivateData privateData = new TransactionPrivateData("TRANS000", "25");
        transientDataMap.put("collectionTransactionPrivateData", genson.serializeBytes(privateData));
        when(stub.getTransient()).thenReturn(transientDataMap);

        assertThat(scbPayments.submitTransaction(ctx)).isNotNull();
    }

    private final class MockKeyValue implements KeyValue {

        private final String key;
        private final String value;

        MockKeyValue(final String key, final String value) {
            super();
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public String getStringValue() {
            return this.value;
        }

        @Override
        public byte[] getValue() {
            return this.value.getBytes();
        }

    }

    private final class MockPaymentResultsIterator implements QueryResultsIterator<KeyValue> {

        private final List<KeyValue> paymentList;

        MockPaymentResultsIterator() {
            super();

            paymentList = new ArrayList<KeyValue>();

            paymentList.add(new MockKeyValue("TRANS000",
                    "{ \"activity\": \"transaction\", \"transactionid\": \"TRANS000\", \"userName\": \"Jon Doe\", "
                            + "\"ccy1\": \"INR\"," + " \"amount1\": \"1000\", \"ccamountcounterpartyamount2\": \"2000\", "
                            + "\"beneficialryname\": \"Jane Doe\", \"benefiaryaccount\": \"SAVING\", \"transactiondate\": \"20-01-2020\", "
                            + "\"cpbic\": \"INR23423ADF\", \"ccy2\": \"SGD\" }"));
            paymentList.add(new MockKeyValue("TRANS001",
                    "{ \"activity\": \"transaction\", \"transactionid\": \"TRANS001\", \"userName\": "
                            + "\"Jon Doe\", \"ccy1\": \"INR\", \"amount1\": \"1000\", \"ccamountcounterpartyamount2\": \"2000\", "
                            + "\"beneficialryname\": \"Jane Doe\", \"benefiaryaccount\": \"SAVING\", \"transactiondate\": \"20-01-2020\", "
                            + "\"cpbic\": \"INR23423ADF\", \"ccy2\": \"SGD\" }"));


        }

        @Override
        public Iterator<KeyValue> iterator() {
            return paymentList.iterator();
        }

        @Override
        public void close() throws Exception {
            // do nothing
        }

    }
}
