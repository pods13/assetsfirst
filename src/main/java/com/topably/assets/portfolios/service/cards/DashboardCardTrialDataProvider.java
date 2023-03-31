package com.topably.assets.portfolios.service.cards;

import com.topably.assets.portfolios.domain.cards.DashboardCard;
import com.topably.assets.portfolios.domain.cards.input.BalanceCard;
import com.topably.assets.portfolios.domain.cards.input.DividendIncomeCard;
import com.topably.assets.portfolios.domain.cards.input.InvestmentYieldCard;
import com.topably.assets.portfolios.domain.cards.input.SectoralDistributionCard;
import com.topably.assets.portfolios.domain.cards.input.allocation.AllocatedByOption;
import com.topably.assets.portfolios.domain.cards.input.allocation.AllocationCard;
import com.topably.assets.portfolios.domain.cards.output.dividend.TimeFrameOption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DashboardCardTrialDataProvider {

    public Collection<DashboardCard> provideCards() {
        var dividendIncomeCard = new DividendIncomeCard()
            .setTimeFrame(TimeFrameOption.QUARTER)
            .setX(0)
            .setY(3)
            .setId("_qynpc9viy")
            .setCols(7)
            .setRows(4)
            .setTitle("Dividend Income")
            .setMinItemCols(3)
            .setMinItemRows(2)
            .setContainerType("DIVIDEND_INCOME");
        var balanceCard = new BalanceCard()
            .setX(4)
            .setY(0)
            .setId("_k4v1pi77k")
            .setCols(3)
            .setRows(2)
            .setTitle("Balance")
            .setMinItemCols(3)
            .setMinItemRows(2)
            .setContainerType("BALANCE");
        var investmentYieldCard = new InvestmentYieldCard()
            .setX(4)
            .setY(2)
            .setId("_by7ckiudg")
            .setCols(2)
            .setRows(1)
            .setTitle("Investment Yield")
            .setMinItemCols(2)
            .setMinItemRows(1)
            .setContainerType("INVESTMENT_YIELD");
        var allocationCard = new AllocationCard()
            .setAllocatedBy(AllocatedByOption.BROKER)
            .setX(0)
            .setY(0)
            .setId("_04v8awga1")
            .setCols(4)
            .setRows(3)
            .setTitle("Allocation")
            .setMinItemCols(3)
            .setMinItemRows(2)
            .setContainerType("ALLOCATION");
        var sectoralDistributionCard = new SectoralDistributionCard()
            .setX(9)
            .setY(1)
            .setId("_s59smpepw")
            .setCols(7)
            .setRows(4)
            .setTitle("Sectoral Distribution")
            .setMinItemCols(3)
            .setMinItemRows(2)
            .setContainerType("SECTORAL_DISTRIBUTION");
        return Set.of(dividendIncomeCard, balanceCard, investmentYieldCard, allocationCard, sectoralDistributionCard);
    }
}
