package com.huawei.olympicranking.repository;

import com.huawei.olympicranking.model.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SportRepository extends JpaRepository<Sport, Long> {
    boolean existsByNameIgnoreCase(String name);
}
