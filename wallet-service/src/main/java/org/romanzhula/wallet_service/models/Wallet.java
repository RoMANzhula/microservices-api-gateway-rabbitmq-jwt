package org.romanzhula.wallet_service.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "wallets")
public class Wallet {

    @Id
    @Column(name = "user_id", unique = true, updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal balance;

}
