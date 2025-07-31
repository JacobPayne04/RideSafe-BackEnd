package com.Jacob.ridesafebackend.dto;
public class DriverRequiredInformationDTO {
    private String driverid;
    private boolean acceptedTerms;
    private String eSign;

    public String getDriverid() {
        return driverid;
    }

    public void setDriverid(String driverid) {
        this.driverid = driverid;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public String geteSign() {
        return eSign;
    }

    public void seteSign(String eSign) {
        this.eSign = eSign;
    }
}

