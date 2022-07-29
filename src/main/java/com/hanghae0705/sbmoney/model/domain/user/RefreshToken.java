package com.hanghae0705.sbmoney.model.domain.user;

import com.hanghae0705.sbmoney.model.domain.baseEntity.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class RefreshToken extends BaseEntity {

    @Id
    @Column(name = "REFRESH_KEY")
    private String key;

    @Column(name = "REFRESH_VALUE")
    private String value;

    @Builder
    public RefreshToken(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public RefreshToken updateValue(String token) {
        this.value = token;
        return this;
    }
}