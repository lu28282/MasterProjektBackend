package com.example.MasterProjekt.pojo;

import java.util.List;

import com.example.MasterProjekt.model.Vulnerability;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
public class SoftwareAndVersion {

    @NonNull
    private String software;

    @NonNull
    private String version;

    @EqualsAndHashCode.Exclude
    private List<Vulnerability> vulnerabilities;
}
