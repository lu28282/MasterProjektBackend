package com.example.MasterProjekt.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Cpe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String cpe23Uri;

    private String software;

    private String version;

    private String versionStartIncluding;

    private String versionEndIncluding;

    private String versionStartExcluding;

    private String versionEndExcluding;
}
