package com.huawei.olympicranking.repository;

import com.huawei.olympicranking.model.MedalRecords;
import com.huawei.olympicranking.model.enums.MedalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedalRecordsRepository extends JpaRepository<MedalRecords, Long> {

    boolean existsByAthleteIdAndSportIdAndMedalType(Long athleteId, Long sportId, MedalType medalType);


    List<MedalRecords> findByAthleteId(Long athleteId);

    List<MedalRecords> findBySportId(Long sportId);
}
