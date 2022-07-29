package com.hanghae0705.sbmoney.model.domain.baseEntity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class CreatedTime {
    @CreatedDate
    private LocalDateTime createdAt;

    public LocalDateTime getCreatedDate() {
        return createdAt;
    }
}
