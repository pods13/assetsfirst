package com.topably.assets.trades.repository.broker;

import com.topably.assets.trades.domain.broker.Broker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrokerRepository extends JpaRepository<Broker, Long> {
}
