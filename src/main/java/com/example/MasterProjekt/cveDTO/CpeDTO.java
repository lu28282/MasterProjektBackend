package com.example.MasterProjekt.cveDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CpeDTO {
    
    String cpe23Uri;

    String versionStartIncluding;

    String versionEndIncluding;
}
