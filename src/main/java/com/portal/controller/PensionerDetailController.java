package com.portal.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.portal.exception.AadharNumberNotFound;
import com.portal.exception.AuthorizationException;
import com.portal.feignclient.PensionerDetailFeignClient;
import com.portal.feignclient.ProcessPensionFeignClient;
import com.portal.model.PensionDetail;
import com.portal.model.PensionerDetail;
import com.portal.model.PensionerInput;
import com.portal.model.ProcessPensionInput;
import com.portal.model.ProcessPensionResponse;

import feign.FeignException;

@CrossOrigin
@Controller
@RequestMapping("/")
public class PensionerDetailController {

	@Autowired
	private PensionerDetailFeignClient pensionerDetailFeignClient;
	
	@Autowired
	private ProcessPensionFeignClient processPensionFeignClient;

	
	
	@GetMapping("/getPensionerByAadhar")
	public ModelAndView showPensionerDetailByAadhar(HttpServletRequest request,@RequestParam long aadhar) throws AuthorizationException, AadharNumberNotFound
	{
		
		if ((String) request.getSession().getAttribute("Authorization") == null) {
			ModelAndView login = new ModelAndView("error-page401");
			return login;
		}
		ModelAndView model = new ModelAndView("show-pensioner-detail-by-aadhar");
		try
		{
			PensionerDetail pensionerDetail = pensionerDetailFeignClient.getPensionerDetailByAadhar((String) request.getSession().getAttribute("Authorization"), aadhar);
			System.out.println(pensionerDetail.getAccountNumber());
			model.addObject("pensionerDetail",pensionerDetail);
		}
		catch(FeignException exx) {
			exx.printStackTrace();
			model.addObject("error","Connection exception. Try Again!");
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addObject("error","My Exception");
		}

		
		return model;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	@GetMapping("/showAllPensioner")
	public ModelAndView getAllPensionerDetail(HttpServletRequest request) throws AuthorizationException
	{
		if ((String) request.getSession().getAttribute("Authorization") == null) {
			ModelAndView login = new ModelAndView("error-page401");
			return login;
		}
		ModelAndView model = new ModelAndView("list-of-pensioner");
		try
		{
			List<PensionerDetail> pensionerDetails = pensionerDetailFeignClient.getAllPensioner((String) request.getSession().getAttribute("Authorization"));
			
			model.addObject("pensionerDetails", pensionerDetails);
			
		}
		catch(FeignException exx) {
			exx.printStackTrace();
			model.addObject("error","Connection exception. Try Again!");
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addObject("error","My Exception");
		}
		return model;
		
	}
	
	@GetMapping("/pensionDetails")
	public ModelAndView getPensionDetails(HttpServletRequest request,@RequestParam long pensionerId,@ModelAttribute("pensionDetail") PensionDetail pensionDetail) throws AuthorizationException, AadharNumberNotFound
	{

		if ((String) request.getSession().getAttribute("Authorization") == null) {
			ModelAndView login = new ModelAndView("error-page401");
			return login;
		}
		ModelAndView model = new ModelAndView("show-pension-detail");
		try
		{
			
			System.out.println("aadhaaar"+pensionerId);
			PensionerDetail pensionerDetail = pensionerDetailFeignClient.getPensionerDetailByAadhar((String) request.getSession().getAttribute("Authorization"), pensionerId);
			System.out.println("PensionerDetailssssssssss..."+pensionerDetail.getName());
			
			PensionerInput pensionerInput = new PensionerInput();
			pensionerInput.setAadharNumber(pensionerDetail.getAadharNumber());
			pensionerInput.setDateOfBirth(pensionerDetail.getDateOfBirth());
			pensionerInput.setName(pensionerDetail.getName());
			pensionerInput.setPan(pensionerDetail.getPan());
			pensionerInput.setPensionType(pensionerDetail.getPensionType());
			
			System.out.println("Nameeeeeeeeeee"+pensionerInput.getName());
			
			pensionDetail = processPensionFeignClient.getPensionDetail((String) request.getSession().getAttribute("Authorization"), pensionerInput);
			
			pensionDetail.setAadharNumber(pensionerDetail.getAadharNumber());
			System.out.println("Nameeeeeeeeeee"+pensionDetail.getName());
			model.addObject("pensionDetail",pensionDetail);
		}
		catch(FeignException exx) {
			exx.printStackTrace();
			model.addObject("error","Connection exception. Try Again!");
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addObject("error","My Exception");
		}
		
		return model;
	}
	
	
	@GetMapping("/processStatusCode")
	public ModelAndView showStatusCode(
			HttpServletRequest request, 
			@ModelAttribute("processPensionInput") ProcessPensionInput processPensionInput,
			@RequestParam long aadharNumber,
			@RequestParam double pensionAmount)
	{
		if ((String) request.getSession().getAttribute("Authorization") == null) {
			ModelAndView login = new ModelAndView("error-page401");
			return login;
		}
		ModelAndView model = new ModelAndView("process-status-code");
		System.out.println("Aadhar Number"+aadharNumber);
		System.out.println("Pension Amount"+pensionAmount);
		System.out.println("Bank Charge"+processPensionInput.getBankCharge());
		try
		{
			if(processPensionInput.getBankCharge() == 0.0)
			{
				processPensionInput.setAadharNumber(aadharNumber);
				processPensionInput.setPensionAmount(pensionAmount);
				model.addObject("processPensionInput",processPensionInput);
			}
			else
			{
				System.out.println("Inside else");
				System.out.println(processPensionInput.getBankCharge());
				ProcessPensionResponse processPensionResponce= processPensionFeignClient.getprocessingCode((String) request.getSession().getAttribute("Authorization"), processPensionInput);
				System.out.println(processPensionResponce.getProcessPensionStatusCode());
				model.addObject("processPensionInput",processPensionInput);
				model.addObject("processPensionResponce",processPensionResponce);
			}
		}
		
		catch(FeignException exx) {
			exx.printStackTrace();
			model.addObject("error","Connection exception. Try Again!");
		}
		
		catch(Exception e)
		{
			System.out.println(e);
		}

		
		return model;
	}
	
	@RequestMapping(value = "/health",method = RequestMethod.GET)
	public ResponseEntity<?> health() throws Exception {
	    try {
	        return ResponseEntity.status(200).body("Ok");
	    } catch (Exception e) {
	        return (ResponseEntity<?>) ResponseEntity.status(null);
	        		//internalServerError().body(e.getMessage());
	    }
	}
	
	
}

