package com.example.MasterProjekt.pojo;

import lombok.Data;
import lombok.NonNull;

@Data
public class Technologie {
    @NonNull
    private String url;

    @NonNull
    private String category;

    @NonNull
    private String app;

    @NonNull
    private String version;
}
