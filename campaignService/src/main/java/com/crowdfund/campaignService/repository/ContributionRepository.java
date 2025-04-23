package com.crowdfund.campaignService.repository;

import com.crowdfund.campaignService.entity.Campaign;
import com.crowdfund.campaignService.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, Long> {
}
