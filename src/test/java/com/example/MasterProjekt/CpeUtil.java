package com.example.MasterProjekt;

import com.example.MasterProjekt.model.Cpe;

public class CpeUtil {
    
    public static Cpe createCpeVersion(String software, String version) {
        Cpe cpe = new Cpe();
        cpe.setSoftware(software);
        cpe.setVersion(version);
        return cpe;
    }

    public static Cpe createCpeStartIncluding(String software, String versionStartIncluding) {
        Cpe cpe = new Cpe();
        cpe.setSoftware(software);
        cpe.setVersionStartIncluding(versionStartIncluding);
        return cpe;
    }

    public static Cpe createCpeStartExcluding(String software, String versionStartExcluding) {
        Cpe cpe = new Cpe();
        cpe.setSoftware(software);
        cpe.setVersionStartExcluding(versionStartExcluding);
        return cpe;
    }
    
    public static Cpe createCpeEndIncluding(String software, String versionEndIncluding) {
        Cpe cpe = new Cpe();
        cpe.setSoftware(software);
        cpe.setVersionEndIncluding(versionEndIncluding);
        return cpe;
    }

    public static Cpe createCpeEndExcluding(String software, String versionEndExcluding) {
        Cpe cpe = new Cpe();
        cpe.setSoftware(software);
        cpe.setVersionEndExcluding(versionEndExcluding);
        return cpe;
    }

    public static Cpe createCpeStartIncludingEndIncluding(String software, String versionStartIncluding, String versionEndIncluding) {
        Cpe cpe = new Cpe();
        cpe.setSoftware(software);
        cpe.setVersionStartIncluding(versionStartIncluding);
        cpe.setVersionEndIncluding(versionEndIncluding);
        return cpe;
    }

    public static Cpe createCpeStartIncludingEndExcluding(String software, String versionStartIncluding, String versionEndExcluding) {
        Cpe cpe = new Cpe();
        cpe.setSoftware(software);
        cpe.setVersionStartIncluding(versionStartIncluding);
        cpe.setVersionEndExcluding(versionEndExcluding);
        return cpe;
    }

    public static Cpe createCpeStartExcludingEndIncluding(String software, String versionStartExcluding, String versionEndIncluding) {
        Cpe cpe = new Cpe();
        cpe.setSoftware(software);
        cpe.setVersionStartExcluding(versionStartExcluding);
        cpe.setVersionEndIncluding(versionEndIncluding);
        return cpe;
    }

    public static Cpe createCpeStartExcludingEndExcluding(String software, String versionStartExcluding, String versionEndExcluding) {
        Cpe cpe = new Cpe();
        cpe.setSoftware(software);
        cpe.setVersionStartExcluding(versionStartExcluding);
        cpe.setVersionEndExcluding(versionEndExcluding);
        return cpe;
    }
}
