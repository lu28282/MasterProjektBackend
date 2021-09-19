package com.example.MasterProjekt.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import java.util.List;
import com.example.MasterProjekt.model.Vulnerability;

@Data
public class Technologie {

    @EqualsAndHashCode.Exclude
    @NonNull
    private String url;

    @EqualsAndHashCode.Exclude
    @NonNull
    private String category;

    @NonNull
    private String app;

    @NonNull
    private String version;

    @EqualsAndHashCode.Exclude
    private List<Vulnerability> vulnerabilities;
}
