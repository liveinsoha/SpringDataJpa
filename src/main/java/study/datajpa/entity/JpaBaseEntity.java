package study.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdTime;
    private LocalDateTime lastModifiedTime;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdTime = now;
        lastModifiedTime = now;
    }

    @PreUpdate
    public void preUpdate() {
        lastModifiedTime = LocalDateTime.now();
    }


}
