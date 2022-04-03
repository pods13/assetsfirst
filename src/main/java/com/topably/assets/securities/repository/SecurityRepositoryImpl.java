package com.topably.assets.securities.repository;


import com.topably.assets.securities.domain.dto.SecurityDto;
import com.topably.assets.securities.domain.SecurityType;
import lombok.RequiredArgsConstructor;
import org.hibernate.transform.ResultTransformer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Repository
@RequiredArgsConstructor
public class SecurityRepositoryImpl implements SecurityRepository {

    private static final String SEARCH_SECURITY_QUERY = "select s.id, s.ticker, c.name as name, 'STOCK' as security_type\n" +
            "from stock s\n" +
            "         join company c on c.id = s.company_id\n" +
            "where s.ticker like concat('%', :search, '%') and 'STOCK' in :securityTypes\n" +
            "UNION\n" +
            "select etf.id, etf.ticker, etf.name as name, 'ETF' as security_type\n" +
            "from etf\n" +
            "where etf.ticker like concat('%', :search, '%') and 'ETF' in :securityTypes";

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<SecurityDto> searchSecurities(String search, Collection<SecurityType> securityTypes) {
        Set<String> types = securityTypes.stream().map(SecurityType::name).collect(toSet());
        return entityManager.createNativeQuery(SEARCH_SECURITY_QUERY)
                .setParameter("search", search)
                .setParameter("securityTypes", types)
                .unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new SecurityDtoResultTransformer())
                .getResultList();
    }

    private static class SecurityDtoResultTransformer implements ResultTransformer {
        @Override
        public Object transformTuple(Object[] tuple, String[] aliases) {
            return SecurityDto.builder()
                    .id(((Number) tuple[0]).longValue())
                    .ticker((String) tuple[1])
                    .name((String) tuple[2])
                    .securityType(SecurityType.valueOf((String) tuple[3]))
                    .build();
        }

        @Override
        public List transformList(List collection) {
            return collection;
        }
    }

}
