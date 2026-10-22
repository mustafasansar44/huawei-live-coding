package com.huawei.olympicranking.repository;

import com.huawei.olympicranking.model.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AthleteRepository extends JpaRepository<Athlete, Long> {
}
