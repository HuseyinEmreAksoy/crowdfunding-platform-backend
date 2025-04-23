package com.crowdfund.campaignService.messager;


import com.crowdfund.campaignService.model.CampaignCreatedEventPayload;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.tx.Contract;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.List;

@Component
public class CampaignEventListener {

    private final Web3j web3j;
    private final RabbitPublisher publisher;

    @Value("${blockchain.contract.address}")
    private String contractAddress;

    public CampaignEventListener(Web3j web3j, RabbitPublisher publisher) {
        this.web3j = web3j;
        this.publisher = publisher;
    }

    @PostConstruct
    public void listenToEvents() {
        Event event = new Event("CampaignCreated", List.of(
                new TypeReference<Uint256>() {},
                new TypeReference<Address>() {},
                new TypeReference<Uint256>() {}
        ));

        EthFilter ethFilter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, contractAddress);

        ethFilter.addSingleTopic(EventEncoder.encode(event));

        web3j.ethLogFlowable(ethFilter).subscribe(log -> {
            try {
                EventValues eventValues = Contract.staticExtractEventParameters(event, log);

                BigInteger campaignId = (BigInteger) eventValues.getNonIndexedValues().getFirst().getValue();
                String owner = "0x" + log.getTopics().getFirst().substring(26);
                BigInteger targetAmount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();

                CampaignCreatedEventPayload payload = new CampaignCreatedEventPayload(
                        campaignId.longValue(), owner, targetAmount
                );

                System.out.println(" Publishing to RabbitMQ: " + payload);
                publisher.publishCampaignCreated(payload);

            } catch (Exception e) {
                System.err.println("Error parsing event log: " + e.getMessage());
                e.printStackTrace();
            }
        }, error -> {
            System.err.println("Error in Web3 event stream: " + error.getMessage());
            error.printStackTrace();
        });



    }
}
