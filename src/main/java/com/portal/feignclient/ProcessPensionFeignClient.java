package com.portal.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.portal.exception.AadharNumberNotFound;
import com.portal.exception.AuthorizationException;
import com.portal.exception.PensionerDetailException;
import com.portal.model.PensionDetail;
import com.portal.model.PensionerInput;
import com.portal.model.ProcessPensionInput;
import com.portal.model.ProcessPensionResponse;

import io.swagger.annotations.ApiOperation;



//@FeignClient(name = "ProcessPension-Microservice", url = "http://localhost:5000/process/api")
@FeignClient(name = "ProcessPension-Microservice", url = "http://pms-890536-penstion-pro-rep-lb-193562482.us-east-1.elb.amazonaws.com/process/api")
public interface ProcessPensionFeignClient {

	@PostMapping("/PensionDetail")
	@ApiOperation(notes = "Returns the Pension Details", value = "Find the pension details")
	public PensionDetail getPensionDetail(
			@RequestHeader(value = "Authorization", required = true) String requestTokenHeader,
			@RequestBody PensionerInput pensionerInput) throws AuthorizationException, PensionerDetailException, AadharNumberNotFound;
	
	
	@PostMapping("/ProcessPension")
	@ApiOperation(notes = "Returns the Process Responce Code(10 or 21)", value = "Find Process Responce Code, If Process code is 10 then Suceess and 21 means not success")
	public ProcessPensionResponse getprocessingCode(
			@RequestHeader(value = "Authorization", required = true) String requestTokenHeader,
			@RequestBody ProcessPensionInput processPensionInput) throws AuthorizationException, AadharNumberNotFound;
}
