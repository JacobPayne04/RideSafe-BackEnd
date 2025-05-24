package com.Jacob.ridesafebackend.dto;

public class DriverRequiredInformationDTO {
	
	 private String firstName;
	    private String lastName;
	    private String licensePlate;
	    private boolean acceptedTerms;
	    private String eSign;
	    private String driverid;
	    
	    
	    
		public String getDriverid() {
			return driverid;
		}
		public void setDriverid(String driverid) {
			this.driverid = driverid;
		}
		public String getFirstName() {
			return firstName;
		}
		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
		public String getLastName() {
			return lastName;
		}
		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
		public String getLicensePlate() {
			return licensePlate;
		}
		public void setLicensePlate(String licensePlate) {
			this.licensePlate = licensePlate;
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
