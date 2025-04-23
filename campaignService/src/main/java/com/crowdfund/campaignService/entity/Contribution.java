package com.crowdfund.campaignService.entity;

import jakarta.persistence.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
public class Contribution {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;
    private int userId;
    private String contributorAddress;
    private BigInteger amount;
    private String txHash;
    private LocalDateTime timestamp;
}
