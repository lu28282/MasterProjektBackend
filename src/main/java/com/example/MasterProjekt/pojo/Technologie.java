package com.example.MasterProjekt.pojo;

import lombok.Data;
import lombok.NonNull;
import java.util.List;
import com.example.MasterProjekt.model.Vulnerability;

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

    private List<Vulnerability> vulnerabilities;
}
