package com.mentoree.iptrace.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static com.mentoree.iptrace.dto.WhoIsDto.*;

@FeignClient(name = "WhoIs-client", url = "${whois.api.url}")
public interface WhoisClient {

    @RequestMapping(method = RequestMethod.GET, value = "/ip_address?")
    WhoIs getIpCountryCode(@RequestParam(value = "serviceKey", required = false, defaultValue = "${whois.api.key}") String serviceKey,
                                    @RequestParam("query") String ipAddress,
                                    @RequestParam("answer") String answer);


}
