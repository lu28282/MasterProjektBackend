package com.example.MasterProjekt.pojo;

import lombok.Data;
import lombok.NonNull;

@Data
public class Technology {

    @NonNull
    private String url;

    @NonNull
    private String category;

    @NonNull
    private SoftwareAndVersion softwareAndVersion;
}
