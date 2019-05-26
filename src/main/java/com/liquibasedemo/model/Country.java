package com.liquibasedemo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;


@Entity
@Table(name = "country")
@Data
@Audited(withModifiedFlag = true)
public class Country
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;



    @ManyToOne
    @JoinColumn(name = "region_id")
    @JsonIgnore
    private Region region;

    public Country()
    {
    }

}
