package com.crowdfund.campaignService.config;

import contract.Crowdfunding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

@Configuration
public class Web3Config {

    @Value("${blockchain.rpc.url}")
    private String rpcUrl;

    @Value("${blockchain.contract.address}")
    private String contractAddress;

    @Value("${blockchain.default.private-key:}")
    private String defaultPrivateKey;

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(rpcUrl));
    }

    @Bean
    public Crowdfunding crowdfunding(Web3j web3j) {
        if (defaultPrivateKey == null || defaultPrivateKey.isBlank()) {
            throw new RuntimeException("Default private key not configured. Set 'blockchain.default.private-key' in properties.");
        }

        Credentials credentials = Credentials.create(defaultPrivateKey);
        return Crowdfunding.load(contractAddress, web3j, credentials, new DefaultGasProvider());
    }
}