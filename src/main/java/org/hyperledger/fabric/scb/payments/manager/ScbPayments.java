/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.scb.payments.manager;

import com.owlike.genson.Genson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.scb.payments.constants.Constants;
import org.hyperledger.fabric.scb.payments.model.PaymentTransaction;
import org.hyperledger.fabric.scb.payments.model.TransactionData;
import org.hyperledger.fabric.scb.payments.model.TransactionPrivateData;
import org.hyperledger.fabric.scb.payments.utility.PaymentsUtility;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ResponseUtils;
import org.hyperledger.fabric.shim.ext.sbe.StateBasedEndorsement;
import org.hyperledger.fabric.shim.ext.sbe.impl.StateBasedEndorsementFactory;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Contract(name = "ScbPayments", info = @Info(title = "Standard Chartered Payments", description =
        "The Standard Chartered Cash Payments", version = "0.0.1-SNAPSHOT", license = @License(name =
        "Apache 2.0 License", url =
        "http://www.apache.org/licenses/LICENSE-2.0.html"), contact = @Contact(email = "abc@scb"
        + ".com", name = "SCb Payments", url = "https://www.scb.com")))
@Default
public final class ScbPayments implements ContractInterface {

    private static Log logger = LogFactory.getLog(ScbPayments.class);
    private final Genson genson = new Genson();

    /**
     * @param ctx the transaction context
     */
    @Transaction
    @SuppressWarnings("checkstyle:ParameterNumber")
    public Chaincode.Response initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        String[] transactionData = null;
        try {
            transactionData = new String[] {
                    "{ \"activity\": \"transaction\", \"transactionid\": \"TRANS1\", \"userName\": \"user1\", "
                            + "\"ccy1\": \"usd\"," + " \"amount1\": \"10000\", \"ccamountcounterpartyamount2\": \"138000\", "
                            + "\"beneficialryname\": \"A\", \"benefiaryaccount\": \"A1\", \"transactiondate\": \"12-12-2018\", "
                            + "\"cpbic\": \"SCBLLSGD\", \"ccy2\": \"sgd\" }",
                    "{ \"activity\": \"transaction\", \"transactionid\": \"TRANS2\", \"userName\": "
                            + "\"user1\", \"ccy1\": \"usd\", \"amount1\": \"10000\", \"ccamountcounterpartyamount2\": \"138000\", "
                            + "\"beneficialryname\": \"A\", \"benefiaryaccount\": \"A1\", \"transactiondate\": \"12-12-2018\", "
                            + "\"cpbic\": \"SCBLLSGD\", \"ccy2\": \"sgd\" }",
                    "{ \"activity\": \"transaction\", \"transactionid\": \"TRANS3\", \"userName\": "
                            + "\"user1\", \"ccy1\": \"usd\", \"amount1\": \"10000\", \"ccamountcounterpartyamount2\": \"138000\", "
                            + "\"beneficialryname\": \"A\", \"benefiaryaccount\": \"A1\", \"transactiondate\": \"12-12-2018\", "
                            + "\"cpbic\": \"SCBLLSGD\", \"ccy2\": \"sgd\" }"};


            for (int i = 0; i < transactionData.length; i++) {
                String key = String.format("TRANS%03d", i);
                TransactionData transact = genson.deserialize(transactionData[i], TransactionData.class);
                String transactState = genson.serialize(transact);
                stub.putStringState(key, transactState);
            }
        } catch (Throwable ex) {
            return ResponseUtils.newErrorResponse("##Could not load data##");
        }
        return ResponseUtils.newSuccessResponse("##Initial data loaded successfully##", genson.serializeBytes(transactionData.length));
    }

    @Transaction
    public Chaincode.Response submitTransaction(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        List<String> args = stub.getParameters();
        logger.info("## creating transaction with transaction size : " + stub.getParameters().size() + "##");
        if (args.size() != Constants.ELEVEN) {
            return ResponseUtils.newErrorResponse("##Incorrect number of arguments. Expecting 11##");
        }
        List<String> validCurrency = new ArrayList<>(Arrays.asList("USD", "INR", "SGD"));
        if (!PaymentsUtility.validateCurrency(validCurrency, args.get(Constants.THREE)) || !PaymentsUtility.validateCurrency(validCurrency, args.get(Constants.TEN))) {
            return ResponseUtils.newErrorResponse("## Currency not supported");
        }

        TransactionData data = new TransactionData(args.get(Constants.ZERO), args.get(Constants.ONE), args.get(Constants.TWO), args.get(Constants.THREE), args.get(Constants.FOUR), args.get(Constants.FIVE), args.get(Constants.SIX), args.get(Constants.SEVEN), args.get(Constants.EIGHT), args.get(Constants.NINE), args.get(Constants.TEN));
        logger.info("Transaction data: " + data);

        /***** Endorsement policy *****/
        setStateBasedEndorsement(stub, data);
        /*******************************/

        /************************************** private data ********************************/
        Map<String, byte[]> map = stub.getTransient();
        if (map == null || map.isEmpty()) {
            logger.info("No Private (Transient) data found");
        } else {
            setPrivateData(stub, map);

        }
        /************************************** private data ********************************/

        stub.putStringState(data.getTransactionid(), genson.serialize(data));
        return ResponseUtils.newSuccessResponse("Transaction Success", genson.serializeBytes(data));
    }

    @Transaction
    public Chaincode.Response queryAllTransaction(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        final String startKey = "TRANS0";
        final String endKey = "TRANS999";
        List<TransactionData> trans = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);
        for (KeyValue result : results) {
            TransactionData transact = genson.deserialize(result.getStringValue(), TransactionData.class);
            trans.add(transact);
        }
        TransactionData[] response = trans.toArray(new TransactionData[trans.size()]);
        logger.info("Transaction data: " + genson.serialize(response));
        return ResponseUtils.newSuccessResponse("Found Data", genson.serializeBytes(response));
    }

    @Transaction
    public Chaincode.Response queryAllTransactionwithPrivateData(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        final String startKey = "TRANS0";
        final String endKey = "TRANS999";
        List<PaymentTransaction> trans = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);
        for (KeyValue result : results) {
            logger.info("Loading data for " + result.getKey());
            TransactionData transact = genson.deserialize(result.getStringValue(), TransactionData.class);
            TransactionPrivateData privateData = getPrivateData(stub, result.getKey());
            trans.add(new PaymentTransaction(transact, privateData));
        }
        PaymentTransaction[] response = trans.toArray(new PaymentTransaction[trans.size()]);
        logger.info("Transaction data: " + genson.serialize(response));
        return ResponseUtils.newSuccessResponse("Found Data", genson.serializeBytes(response));
    }

    @Transaction
    public Chaincode.Response richQueryExecution(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        List<String> args = stub.getParameters();
        if (args == null || args.size() < 1) {
            return ResponseUtils.newErrorResponse("Incorrect number of arguments. Expecting ");
        }
        QueryResultsIterator<KeyValue> result = stub.getQueryResult(args.get(0));
        if (result == null) {
            return ResponseUtils.newErrorResponse("No data found");
        } else {
            List<String> queryData = new ArrayList<>();
            Iterator<KeyValue> iterator = result.iterator();
            while (iterator.hasNext()) {
                queryData.add(iterator.next().getStringValue());
            }
            logger.info("##Data count: " + queryData.size() + "##");
            return ResponseUtils.newSuccessResponse("Found data", genson.serializeBytes(queryData));
        }
    }


    public TransactionPrivateData getPrivateData(final ChaincodeStub stub, final String key) {
        TransactionPrivateData privateData = genson.deserialize(stub.getPrivateDataUTF8("collectionTransactionPrivateData", key), TransactionPrivateData.class);
        logger.info("Private Data for :" + key + " : " + genson.serialize(privateData));
        return privateData;
    }

    @Transaction
    public Chaincode.Response queryTransactionPrivateDataById(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        List<String> args = stub.getParameters();
        if (args.size() != 1) {
            throw new ChaincodeException("Incorrect number of arguments",
                    ScbPaymentErrors.INCORRECT_NUMBER_ARGUMENTS.toString());
        }
        TransactionPrivateData privateData = null;
        String transactionId = args.get(0);
        logger.info("Fetching private data for id:" + transactionId);
        try {
            privateData = getPrivateData(stub, transactionId);
            if (privateData == null) {
                throw new ChaincodeException("Could not get data for private data id : " + transactionId, ScbPaymentErrors.DATA_NOT_FOUND.toString());
            }
        } catch (Exception ex) {
            return ResponseUtils.newErrorResponse("Not Data Found", genson.serializeBytes("No Data"));
        }
        logger.info("Data found:" + privateData);
        return ResponseUtils.newSuccessResponse("Found private data", genson.serializeBytes(privateData));
    }

    public Chaincode.Response addOrgs(final ChaincodeStub stub, final String... parameters) {
        try {
            logger.info("Invoking addOrgs:" + (parameters.length - 1) + " for transactionid:"
                    + parameters[0]);
            if (parameters.length < 2) {
                return ResponseUtils.newErrorResponse("No orgs to add specified");
            }

            byte[] epBytes;
            epBytes = stub.getStateValidationParameter(parameters[0]);
            StateBasedEndorsement ep = StateBasedEndorsementFactory.getInstance().newStateBasedEndorsement(epBytes);
            if (parameters[1] == null) {
                logger.info("Using channel endorsement");
                stub.setStateValidationParameter(parameters[0], null);
            } else {
                logger.info("Using key based endorsement");
                for (int i = 1; i < parameters.length; i++) {
                    ep.addOrgs(StateBasedEndorsement.RoleType.RoleTypePeer, parameters[i]);
                }
                epBytes = ep.policy();
                stub.setStateValidationParameter(parameters[0], epBytes);
            }
            return ResponseUtils.newSuccessResponse(new byte[] {});
        } catch (Throwable e) {
            return ResponseUtils.newErrorResponse(e);
        }
    }

    public Chaincode.Response listOrgs(final ChaincodeStub stub, final String parameter) {
        try {
            logger.info("Invoking listOrgs");
            if (parameter.isEmpty()) {
                return ResponseUtils.newErrorResponse("No key specified");
            }

            byte[] epBytes;
            epBytes = stub.getStateValidationParameter(parameter);
            StateBasedEndorsement ep = StateBasedEndorsementFactory.getInstance().newStateBasedEndorsement(epBytes);

            List<String> orgs = ep.listOrgs();
            logger.info("List of Endorsing orgs count: " + orgs.size() + " : " + genson.serialize(orgs));
            return ResponseUtils.newSuccessResponse("Found list", genson.serializeBytes(orgs));
        } catch (Throwable e) {
            return ResponseUtils.newErrorResponse(e);
        }
    }

    public void setStateBasedEndorsement(final ChaincodeStub stub, final TransactionData data) {
        if (data.getCcy2().equalsIgnoreCase("SGD")) {
            addOrgs(stub, data.getTransactionid(), "Org2MSP", "Org1MSP");
            logger.info("Endorsing peers are:" + genson.deserialize(listOrgs(stub, data.getTransactionid()).getPayload(), List.class));
        }
    }

    @Transaction
    public Chaincode.Response queryTransactionKeyHistory(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        List<String> args = stub.getParameters();
        if (args.size() == 0 || args.get(0).isEmpty() || args.get(0) == null) {
            return ResponseUtils.newErrorResponse("## Search key not found##");
        }
        QueryResultsIterator<KeyModification> queryResult = stub.getHistoryForKey(args.get(0));
        if (queryResult == null) {
            return ResponseUtils.newErrorResponse("No Data Found");
        } else {
            List<String> queryData = new ArrayList<>();
            Iterator<KeyModification> iterator = queryResult.iterator();
            while (iterator.hasNext()) {
                queryData.add(iterator.next().getStringValue());
            }
            logger.info("##Data count: " + queryData.size() + "##");
            return ResponseUtils.newSuccessResponse("Found data", genson.serializeBytes(queryData));
        }
    }

    public void setPrivateData(final ChaincodeStub stub, final Map<String, byte[]> privateDataMap) {
        logger.info("Found Transient data: " + new String(privateDataMap.get("collectionTransactionPrivateData")));
        TransactionPrivateData transactPrivateData = genson.deserialize(privateDataMap.get("collectionTransactionPrivateData"), TransactionPrivateData.class);
        if (transactPrivateData.getTransactionid() == null || transactPrivateData.getTransactionid().isEmpty()) {
            logger.debug("Transaction id not found, exiting state db");
            throw new ChaincodeException("Transaction id not found in state db", ScbPaymentErrors.TRANSACTION_NOT_EXISTS.toString());
        }
        stub.putPrivateData("collectionTransactionPrivateData", transactPrivateData.getTransactionid(), genson.serialize(transactPrivateData));
    }

    private enum ScbPaymentErrors {
        PAYMENT_NOT_FOUND, PAYMENT_ALREADY_EXISTS, CURRENCY_NOT_SUPPORTED, INCORRECT_NUMBER_ARGUMENTS, DATA_NOT_FOUND, TRANSACTION_NOT_EXISTS
    }

}
