package com.crowdfund.campaignService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "contributions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contribution {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;
    private Long userId;
    private String contributorAddress;
    private BigInteger amount;
    private String txHash;
    private LocalDateTime timestamp;
}
