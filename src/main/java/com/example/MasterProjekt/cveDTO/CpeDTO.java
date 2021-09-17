package com.example.MasterProjekt.cveDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CpeDTO {

    private long id;

    private String cpe23Uri;

    private String software;

    private String version;

    private String versionStartIncluding;

    private String versionEndIncluding;

    private String versionStartExcluding;

    private String versionEndExcluding;
}
