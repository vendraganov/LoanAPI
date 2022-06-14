package com.example.loan_api.idempotency;

import com.example.loan_api.helper.Constants;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.loan_api.helper.Constants.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="idempotent_keys")
public class IdempotentKey {

    @Id
    @Type(type = UUID_TYPE)
    @Column(name = IDEMPOTENT_KEY)
    private UUID idempotentKey;

    @NotNull
    @Column(name = DOMAIN)
    private String domain;

    @NotNull
    @Column(name = METHOD_NAME)
    private String methodName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = Constants.STATUS)
    private IdempotentKeyStatus status;

    @NotNull
    @Column(name = USED_ON)
    private LocalDateTime usedOn;
}
