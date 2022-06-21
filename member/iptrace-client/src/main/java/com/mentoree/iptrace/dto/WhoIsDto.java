package com.mentoree.iptrace.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WhoIsDto {

    private WhoIs result;

    @Getter
    @Setter
    public static class WhoIs {
        private String query;
        private String countryCode;
    }

}
