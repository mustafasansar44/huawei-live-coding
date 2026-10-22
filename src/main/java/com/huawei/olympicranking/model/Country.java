package com.huawei.olympicranking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huawei.olympicranking.model.enums.CountryCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "countries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Country extends BaseClass {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 10)
    private CountryCode countryCode;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true) // eğer olurda bir Coutry içerisinde bir athlete silinirse ve sonra repo.save yapılırsa orphanRemoval yetim olanları da sildirir.
    @JsonIgnore // anti recursion ve lazy exception için
    @Builder.Default
    private List<Athlete> athletes = new ArrayList<>();

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private List<MedalRecords> medalRecords = new ArrayList<>();
}