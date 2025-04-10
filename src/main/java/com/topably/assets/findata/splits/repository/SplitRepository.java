package com.topably.assets.findata.splits.repository;

import com.topably.assets.findata.splits.domain.Split;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface SplitRepository extends JpaRepository<Split, Long> {

    Collection<Split> findAllByInstrument_IdOrderByExDate(Long instrumentId);

    @Query(nativeQuery = true, value = """
            select *
            from (select s.*, row_number() over (partition by s.instrument_id order by s.ex_date desc) as rn
                  from split s) latest_split
            where rn = 1
            """)
    List<Split> findInstrumentsLastSplit();
}
