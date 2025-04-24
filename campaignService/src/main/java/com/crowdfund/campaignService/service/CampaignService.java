package com.crowdfund.campaignService.service;

import com.crowdfund.campaignService.model.request.ContributeRequest;
import com.crowdfund.campaignService.model.request.CreateCampaignRequest;
import com.crowdfund.campaignService.model.response.ContributeResponse;
import com.crowdfund.campaignService.model.response.CreateCampaignResponse;
import contract.Crowdfunding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class CampaignService {


    private final Web3j web3j;

    @Value("${blockchain.contract.address}")
    private String contractAddress;

    public CampaignService(Web3j web3j) {
        this.web3j = web3j;
    }

    public CreateCampaignResponse createCampaign(CreateCampaignRequest request, String privateKeyHex) throws Exception {
        StaticGasProvider gasProvider = new StaticGasProvider(
                Convert.toWei("20", Convert.Unit.GWEI).toBigInteger(), // gas price
                BigInteger.valueOf(4_500_000) // gas limit under block limit (typical 30M for Sepolia)
        );

        Credentials credentials = Credentials.create(privateKeyHex);
        Crowdfunding contract = Crowdfunding.load(
                contractAddress,
                web3j,
                credentials,
                new DefaultGasProvider()

        );
        long duration = Duration.between(LocalDateTime.now(), request.getDeadline()).getSeconds();

        TransactionReceipt receipt = contract.createCampaign(request.getTitle(), request.getTargetAmount(), BigInteger.valueOf(duration)).send();

        return new CreateCampaignResponse(receipt.getTransactionHash(), receipt.getStatus());

    }

    public ContributeResponse contributeCampaign(ContributeRequest request, String privateKeyHex) throws Exception {

        StaticGasProvider gasProvider = new StaticGasProvider(
                Convert.toWei("20", Convert.Unit.GWEI).toBigInteger(), // gas price
                BigInteger.valueOf(4_500_000) // gas limit under block limit (typical 30M for Sepolia)
        );

        Credentials credentials = Credentials.create(privateKeyHex);
        Crowdfunding contract = Crowdfunding.load(
                contractAddress,
                web3j,
                credentials,
                new DefaultGasProvider()
        );
        BigInteger weiAmount = Convert.toWei(request.getAmount().toString(), Convert.Unit.WEI).toBigInteger();

        TransactionReceipt receipt = contract.contributeCampaign(
                request.getContractId(),
                weiAmount
        ).send();
        return new ContributeResponse(receipt.getTransactionHash(), receipt.getStatus());

    }
}