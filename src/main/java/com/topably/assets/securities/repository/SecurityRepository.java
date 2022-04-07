package com.topably.assets.securities.repository;

import com.topably.assets.securities.domain.Security;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface SecurityRepository extends JpaRepository<Security, Long> {

    @Query(nativeQuery = true, value = "select *\n" +
            "from security s\n" +
            "where s.ticker like concat('%', :search, '%')\n" +
            "  and security_type in :securityTypes\n")
    Collection<Security> searchSecurityByTickerLikeAndTypeIn(String search, Collection<String> securityTypes);

    @Query(nativeQuery = true, value = "select *\n" +
            "from security s\n" +
            "         join exchange e on e.id = s.exchange_id\n" +
            "where security_type in :securityTypes\n" +
            "  and e.code in :exchangeCodes\n")
    Collection<Security> findAllBySecurityTypesInAndExchangeCodesIn(Collection<String> securityTypes,
                                                                    Collection<String> exchangeCodes);

    Security findByTickerAndExchange_Code(String ticker, String exchange);

}
