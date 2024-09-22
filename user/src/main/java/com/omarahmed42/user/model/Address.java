package com.omarahmed42.user.model;

import java.io.Serializable;

import org.hibernate.annotations.GenericGenerator;

import com.omarahmed42.user.generator.SnowflakeUIDGenerator;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
public class Address implements Serializable {
    @Id
    @GenericGenerator(name = "snowflake_id_generator", type = SnowflakeUIDGenerator.class)
    @GeneratedValue(generator = "snowflake_id_generator")
    private Long id;

    @Basic(optional = false)
    @Column(name = "city", length = 250, nullable = false)
    private String city;

    @Basic(optional = false)
    @Column(name = "country", length = 70, nullable = false)
    private String country;

    @Basic(optional = false)
    @Column(name = "address_line", nullable = false)
    private String addressLine;

    @Column(name = "zip_code", length = 20)
    private String zipCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
