package dev.wilsonblanco.reportgenerator.models;


import dev.wilsonblanco.reportgenerator.dto.ConnectionCredentialsDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "db_connections")
public class ConnectionCredentialsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Column(nullable = false)
    private String alias;

    @Column(nullable = false)
    private String dbType;

    @Column(nullable = false)
    private String host;

    @Column(nullable = false)
    private int port;

    @Column(nullable = false)
    private String dbName;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, length = 500)
    private String password;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.publicId == null) {
            this.publicId = UUID.randomUUID();
        }
    }

    public ConnectionCredentialsDto toConnectionRequest() {

        return new ConnectionCredentialsDto(
                this.publicId.toString(),
                this.alias,
                this.dbType,
                this.host,
                this.port,
                this.dbName,
                this.username,
                this.password
        );
    }
}
