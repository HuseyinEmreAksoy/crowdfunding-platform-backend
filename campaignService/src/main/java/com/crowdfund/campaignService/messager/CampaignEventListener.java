package com.crowdfund.campaignService.messager;


import com.crowdfund.campaignService.model.CampaignContributeEventPayload;
import com.crowdfund.campaignService.model.CampaignCreatedEventPayload;
import contract.Crowdfunding;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.tx.gas.DefaultGasProvider;

@Component
public class CampaignEventListener {

    private final Web3j web3j;
    private final RabbitPublisher publisher;

    @Value("${blockchain.default.private-key}")
    private String privateKey;

    @Value("${blockchain.contract.address}")
    private String contractAddress;

    public CampaignEventListener(Web3j web3j, RabbitPublisher publisher) {
        this.web3j = web3j;
        this.publisher = publisher;
    }

    @PostConstruct
    public void listenToEvents() {
        Credentials credentials = Credentials.create(privateKey);
        Crowdfunding contract = Crowdfunding.load(contractAddress, web3j, credentials, new DefaultGasProvider());

        contract.campaignCreatedEventFlowable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                .subscribe(event -> {
                    CampaignCreatedEventPayload payload = new CampaignCreatedEventPayload(
                            event.campaignId.longValue(),
                            event.owner,
                            event.targetAmount
                    );
                    System.out.println("Publishing to RabbitMQ (created): " + payload);
                    publisher.publishCampaignCreated(payload);
                },error -> {
                    System.err.println("Error in campaignCreatedEventFlowable: " + error.getMessage());
                    error.printStackTrace();
                });

        contract.contributionMadeEventFlowable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                .subscribe(event -> {
                    CampaignContributeEventPayload payload = new CampaignContributeEventPayload(
                            event.campaignId.longValue(),
                            event.contributer,
                            event.amount
                    );

                    System.out.println("Publishing to RabbitMQ (contribute): " + payload);
                    publisher.publishCampaignContributed(payload);
                },error -> {
                    System.err.println("Error in contributionMadeEventFlowable: " + error.getMessage());
                    error.printStackTrace();
                });

    }
}
