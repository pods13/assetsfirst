package com.topably.assets.instruments.repository.instrument;

import com.topably.assets.instruments.domain.instrument.FX;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FXRepository extends JpaRepository<FX, Long> {
}
