package com.portal.feignclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.portal.exception.AadharNumberNotFound;
import com.portal.exception.AuthorizationException;
import com.portal.model.PensionerDetail;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

//@FeignClient(name = "PensionerDetail-Microservice", url = "http://localhost:6001/pensioner/api")
@FeignClient(name = "PensionerDetail-Microservice", url = "http://890536-pensionerdetail-lb-1961886833.us-east-1.elb.amazonaws.com/pensioner/api")
public interface PensionerDetailFeignClient {

	@GetMapping("/getAllPensioner")
	@ApiOperation(notes = "Returns the Pension Details", value = "Find All Pensioner Details")
	public List<PensionerDetail> getAllPensioner(
			@RequestHeader(value = "Authorization", required = true) String requestTokenHeader) throws AuthorizationException;
	
	@GetMapping("/PensionerDetailByAadhaar/{aadharNumber}")
	@ApiOperation(notes = "Returns the Pension Details by aadharCard Number", value = "Find Pension Details By Aadhar Card number")
	public PensionerDetail getPensionerDetailByAadhar(
			@RequestHeader(value = "Authorization", required = true) String requestTokenHeader,
			@ApiParam(name = "aadharNumber", value = "Aadhar Card Number") 
			@PathVariable long aadharNumber) throws AuthorizationException, AadharNumberNotFound;
}
