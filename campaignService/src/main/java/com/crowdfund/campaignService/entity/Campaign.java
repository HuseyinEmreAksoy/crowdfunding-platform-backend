package com.crowdfund.campaignService.entity;

import com.crowdfund.campaignService.states.CampaignStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "campaigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long campaignId;

    private String ownerAddress;

    private BigInteger targetAmount;

    private BigInteger raisedAmount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private CampaignStatus status;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
    private List<Contribution> contributions;
}
