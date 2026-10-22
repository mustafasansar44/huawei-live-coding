package com.huawei.olympicranking.repository;

import com.huawei.olympicranking.model.Country;
import com.huawei.olympicranking.model.enums.CountryCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    boolean existsByCountryCode(CountryCode countryCode);
}
