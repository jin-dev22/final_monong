package com.kh.monong.member.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.monong.common.HelloSpringUtils;
import com.kh.monong.common.MailUtils;
import com.kh.monong.direct.model.dto.DirectProduct;
import com.kh.monong.inquire.model.dto.Inquire;
import com.kh.monong.member.model.dto.Member;
import com.kh.monong.member.model.dto.Seller;
import com.kh.monong.member.model.dto.SellerInfo;
import com.kh.monong.member.model.dto.SellerInfoAttachment;
import com.kh.monong.member.model.service.MemberService;
import com.kh.monong.subscribe.model.dto.Subscription;
import com.kh.monong.subscribe.model.dto.SubscriptionOrder;
import com.kh.monong.subscribe.model.dto.SubscriptionProduct;
import com.kh.monong.subscribe.model.dto.Vegetables;
import com.kh.monong.subscribe.model.service.SubscribeService;
import com.kh.security.model.service.MemberSecurityService;

import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping("/member")
@Slf4j
public class MemberController {
	@Autowired
	private MemberService memberService;
	@Autowired
	private MemberSecurityService memberSecurityService;
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;

	//-------------수진 시작		
	@Autowired
	ServletContext application;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@GetMapping("/selectEnrollType.do")
	public void selectEnrollType() {		
	}
	
	@GetMapping("/memberEnroll.do")
	public void memberEnroll() {
	}
	
	@GetMapping("/sellerEnroll.do")
	public void sellerEnroll() {
	}
	
	@PostMapping("/checkIdDuplicate.do")
	public ResponseEntity<?> checkIdDuplicate(@RequestParam String memberId) {
		Member member = memberService.selectMemberById(memberId);
		boolean available = member == null;
		
		Map<String, Object> map = new HashMap<>();
		map.put("memberId", memberId);
		map.put("available", available);
		
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}
	
	@PostMapping("/checkEmailDuplicate.do")
	public ResponseEntity<?> checkEmailDuplicate(@RequestParam String email){
		Member member = memberService.selectMemberByEmail(email);
		boolean available = member == null;
		
		Map<String, Object> map = new HashMap<>();
		map.put("memberEmail", email);
		map.put("available", available);
		log.debug("result map = {}", map);
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}
	
	@PostMapping("/memberEnroll.do")
	public String memberEnroll(Member member, RedirectAttributes redirectAttr) {
		try {
			log.debug("member = {}", member);
			
			// 비밀번호 암호화
			String rawPassword = member.getPassword();
			String encodedPassword = bcryptPasswordEncoder.encode(rawPassword);
			member.setMemberPassword(encodedPassword);
			log.debug("encodedPassword = {}", encodedPassword);
			
			//회원권한 db입력용 설정.
			Map<String, Object> memberAuthMap = new HashMap<>();
			memberAuthMap.put("memberId", member.getMemberId());
			memberAuthMap.put("memberAuth", "ROLE_MEMBER");
			int result = memberService.insertMember(memberAuthMap, member);
			redirectAttr.addFlashAttribute("msg", "회원 가입이 정상적으로 처리되었습니다.");
			return "redirect:/member/memberLogin.do";
		} catch(Exception e) {
			log.error("회원가입 오류 : " + e.getMessage(), e);
			e.printStackTrace();
			throw e;
		}
	}
	
	@PostMapping("/sellerEnroll.do")
	public String sellerEnroll(
			Seller seller,
			@RequestParam String sellerName,
			@RequestParam String sellerRegNo, 
			@RequestParam(name="sellerRegFile", required = false) MultipartFile sellerRegFile,
			RedirectAttributes redirectAttr) throws Exception {
	
		log.debug("member = {}", seller);
		log.debug("regNo = {}", sellerRegNo);
		log.debug("sellerRegFile = {}", sellerRegFile);
		
		seller.setSellerInfo(SellerInfo.builder()
									.memberId(seller.getMemberId())
									.sellerRegNo(sellerRegNo)
									.sellerName(sellerName)
									.build());
		try {
			// 비밀번호 암호화
			String rawPassword = seller.getPassword();
			String encodedPassword = bcryptPasswordEncoder.encode(rawPassword);
			seller.setMemberPassword(encodedPassword);
			log.debug("encodedPassword = {}", encodedPassword);
			
			//회원권한 db입력용
			Map<String, Object> memberAuthMap = new HashMap<>();
			memberAuthMap.put("memberId", seller.getMemberId());
			memberAuthMap.put("memberAuth", "ROLE_SELLER");
			
			//사업자등록증 서버컴퓨터 저장
			String renamedFilename = fileSaver("/resources/upload/sellerRegFiles", sellerRegFile.getOriginalFilename(), sellerRegFile);
			
			
			//판매자정보 저장
			seller.setAttachment(SellerInfoAttachment.builder()
					.memberId(seller.getMemberId())
					.originalFilename(sellerRegFile.getOriginalFilename())
					.renamedFilename(renamedFilename)
					.build());	
			log.debug("seller = {}", seller);
			int result = memberService.insertSeller(memberAuthMap, seller);
			
			redirectAttr.addFlashAttribute("msg", "회원 가입이 정상적으로 처리되었습니다.");
			return "redirect:/member/memberLogin.do";
		} catch(Exception e) {
			log.error("회원가입 오류 : " + e.getMessage(), e);
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * @param directoryPath 저장경로 문자열
	 * @param originalFilename 파일원래이름
	 * @param sellerRegFile 파일
	 * @return renamedFilename 파일저장이름
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public String fileSaver(String directoryPath, String originalFilename, 
			MultipartFile sellerRegFile) throws IllegalStateException, IOException {
		
		String saveDirectory = application.getRealPath(directoryPath);
		log.debug("saveDirectory = {}",saveDirectory);
		String renamedFilename = HelloSpringUtils.getRenamedFilename(originalFilename);
		File destFile = new File(saveDirectory, renamedFilename);
		log.debug("destFile = {}",destFile);
		sellerRegFile.transferTo(destFile);
		
		return renamedFilename;
	}
	
	@PostMapping("/sendEmailKey.do")
	public ResponseEntity<?> sendEmailKey(@RequestParam String email){
		Map<String, Object> map = new HashMap<>();
		try {
			MailUtils sendMail = new MailUtils(mailSender);
			String identifyKey = new TempKey().getKey(4,false);
			map.put("identifyKey", identifyKey);
			map.put("memberEmail", email);
			
			sendMail.setSubject("모농모농 회원가입 이메일 인증코드입니다.");
			sendMail.setText("<h1>이메일 인증코드</h1>"
					+ "<br />"+ "인증코드는"
							+ "<br /><strong>"+identifyKey+"</strong>입니다."
							+ "<br />감사합니다!"
								);
			sendMail.setFrom("sooappeal31@gmail.com", "모농모농");
			sendMail.setTo(email);
			sendMail.send();
			//db저장
			int result = memberService.insertEmailIdentify(map);
			log.debug("after db ={}", map.remove("identifyKey"));
			map.put("msg", "입력하신 이메일로 인증코드가 전송되었습니다.");
			
		} catch(Exception e) {
			log.error("이메일 인증코드 전송오류 : " + e.getMessage(), e);
		}
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}
	
	@PostMapping("/checkEmailKey.do")
	public ResponseEntity<?> checkEmailKey(@RequestParam String email, @RequestParam String emailKey){
		Map<String, Object> map = new HashMap<>();
		String key = memberService.getEmailKey(email);
		log.debug("db key ={}", key);
		boolean isIdentified = emailKey.equals(key);
		String msg = isIdentified? "이메일 인증 완료!" : "인증코드가 일치하지 않아요. 다시 확인해주세요.";
		map.put("msg", msg);
		map.put("isIdentified", isIdentified);
		
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}
	
	@GetMapping("/sellerMyPage.do")
	public void sellerMypage() {
	}
	
	@GetMapping("/sellerProdList.do")
	public void sellerProdList(Authentication authentication, 
								@RequestParam(defaultValue = "1") int cPage, 
								@RequestParam(defaultValue = "판매중") String dSaleStatus,
								Model model, HttpServletRequest request) {
		Member member = (Member) (authentication.getPrincipal());
		Map<String, Object> param = new HashMap<>();
		int limit = 5;
		param.put("cPage", cPage);
		param.put("limit", limit);
		param.put("memberId", member.getMemberId());
		param.put("dSaleStatus", dSaleStatus);
		log.debug("param = {}", param);
		List<DirectProduct> prodList = memberService.selectDirectListBySellerId(param);
		log.debug("prodList = {}", prodList);
		
		model.addAttribute("prodList", prodList);
		
		int totalContent = memberService.getTotalProdCntBySeller(param);
		log.debug("totalContent = {}", totalContent);
		String url = request.getRequestURI(); 
		String pagebar = HelloSpringUtils.getPagebar(cPage, limit, totalContent, url);
		model.addAttribute("pagebar", pagebar);
		
		log.debug("model = {}", model);
	};
	
	@GetMapping("/sellerProdOrderList.do")
	public void sellerProdOrderList(@RequestParam String prodNo, 
									@RequestParam(required = false) String startDate, 
									@RequestParam(required = false) String endDate,
									@RequestParam(defaultValue = "1") int cPage,
									Model model, HttpServletRequest request) {
		Map<String, Object> param = new HashMap<>();
		int limit = 5;
		param.put("cPage", cPage);
		param.put("limit", limit);
		param.put("prodNo", prodNo);
		//검색기간 설정시 빈 문자열 전달 방지
		if(startDate == "" || endDate == "") {
			startDate = null;
			endDate = null;
		}
		//endDate +1 해서 db조회
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 
		if(endDate != null && endDate != "") {
			LocalDate _endDate = LocalDate.parse(endDate, dtf);
			_endDate = _endDate.plusDays(1);
			endDate =  _endDate.toString();
			log.debug("endDate={}",endDate);
		}
		param.put("startDate", startDate);
		param.put("endDate",endDate);
		log.debug("param = {}",param);
		
		//endDate 다시 -1해서 view에 전달
		if(endDate != null && endDate != "") {
			LocalDate _endDate = LocalDate.parse(endDate, dtf);
			_endDate = _endDate.plusDays(-1);
			endDate =  _endDate.toString();
		}
		model.addAttribute("startDate",startDate);
		model.addAttribute("endDate",endDate);
		
		List<Map<String, Object>> orderList = memberService.selectOrderListByProdNo(param);
		model.addAttribute("orderList", orderList);
		log.debug("orderList={}", orderList);
		
		String prodName = memberService.selectProdNameByNo(prodNo);
		model.addAttribute("prodName",prodName);
		
		int totalContent = memberService.getTotalOrderCntByProdNo(param);
		log.debug("totalContent = {}", totalContent);
		String url = request.getRequestURI(); 
		String pagebar = HelloSpringUtils.getPagebar(cPage, limit, totalContent, url);
		model.addAttribute("pagebar", pagebar);
	}
	
	@PostMapping("/updateDOrderStatus.do")
	public ResponseEntity<?> updateDOrderStatus(@RequestParam String orderStatus, @RequestParam String dOrderNo,
												@RequestParam String dOrderMember, @RequestParam String dProdNo,
												@RequestParam String dProdName) {
		log.debug("newStatus = {}",orderStatus);
		log.debug("dOrderNo = {}", dOrderNo);
		log.debug("dOrderMember = {}",dOrderMember);
		log.debug("dProdNo = {}",dProdNo);
		log.debug("dProdName = {}",dProdName);
		
		Map<String, Object> param = new HashMap<>();
		param.put("newStatus", orderStatus);
		param.put("dOrderNo", dOrderNo);	
		//주문내역 상태변경
		int result = memberService.updateDOrderStatus(param);
		
		//알림정보저장
		
		
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	@GetMapping("/sellerUpdate.do")
	public void sellerUpdate() {
		
	}
	@PostMapping("/sellerUpdate.do")
	public String sellrUpdate(@ModelAttribute Seller seller, 
							 @RequestParam String sellerName,
							 @RequestParam String sellerRegNo, 
							 RedirectAttributes redirectAttr,
							 @RequestParam(name="sellerRegFile", required = false) MultipartFile sellerRegFile,
							 @RequestParam(required = false) long delFileNo,
							 Model model) throws Exception {
		
		seller.setSellerInfo(SellerInfo.builder()
									.memberId(seller.getMemberId())
									.sellerRegNo(sellerRegNo)
									.sellerName(sellerName)
									.build());
		log.debug("seller = {}", seller);
		
		int result = 0;
		if(!sellerRegFile.isEmpty() && delFileNo != 0) {
			try {
			String directory = "/resources/upload/sellerRegFiles";
			String saveDirectory = application.getRealPath(directory);
			log.debug("sellerRegFile = {}",sellerRegFile);
			//첨부파일 삭제
			SellerInfoAttachment attach = memberService.selectSellerInfoAttachment(delFileNo);
			File delFile = new File(saveDirectory, attach.getRenamedFilename());
			boolean isDeleted = delFile.delete();
			log.debug("{} isDel? = {}",attach.getOriginalFilename() ,isDeleted);
			
			//업로드파일 저장, db행 삭제
			String renamedFilename = fileSaver(directory, sellerRegFile.getOriginalFilename(), sellerRegFile);
			log.debug("renamedFilename!!!!!!!!!!!! = {}",renamedFilename);
			if(isDeleted) {
				result = memberService.deleteSellerAttachment(delFileNo);//
				log.debug("isDel in DB? = {}", result>0);	
			}
			
			//seller에 attachment설정
			seller.setAttachment(SellerInfoAttachment.builder()
					.memberId(seller.getMemberId())
					.originalFilename(sellerRegFile.getOriginalFilename())
					.renamedFilename(renamedFilename)
					.build());
			log.debug("seller = {}", seller);
			} catch (Exception e) {
				log.error("판매자 정보수정 첨부파일오류 : " + e.getMessage(), e);
				throw e;
			}
		}
		
		//판매자 정보 변경		
		result = memberService.updateSeller(seller);
		UserDetails updatedMember = memberSecurityService.loadUserByUsername(seller.getMemberId());
		
		Authentication updateAthentication = new UsernamePasswordAuthenticationToken(
				updatedMember,
				updatedMember.getPassword(),
				updatedMember.getAuthorities()
				);
		SecurityContextHolder.getContext().setAuthentication(updateAthentication);
		log.debug("member={}", updatedMember);
		redirectAttr.addFlashAttribute("msg", "회원 정보가 수정되었습니다!");
		return "redirect:/member/sellerUpdate.do";
		
	}
	
	@GetMapping(path = "/fileDownload.do", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public Resource fileDownload(@RequestParam int no, HttpServletResponse response) throws IOException {
		SellerInfoAttachment attach = memberService.selectSellerInfoAttachment(no);
		log.debug("attach = {}", attach);
		
		String saveDirectory = application.getRealPath("/resources/upload/sellerRegFiles");
		File downFile = new File(saveDirectory, attach.getRenamedFilename());
		String location = "file:" + downFile; // File#toString은 파일의 절대경로 반환
		Resource resource = resourceLoader.getResource(location);
		log.debug("resource = {}", resource);
		log.debug("resource#file = {}", resource.getFile());
		
		// 응답헤더 작성
		response.setContentType("application/octet-stream; charset=utf-8");
		String filename = new String(attach.getOriginalFilename().getBytes("utf-8"), "iso-8859-1");
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
		
		return resource;
	}
	
	@GetMapping("/memberInquireList.do")
	public void memberInqurieList(Authentication authentication,
								@RequestParam(defaultValue = "1") int cPage,
								Model model, HttpServletRequest request) {
		String memberId = authentication.getName();
		log.debug("memberId ={}", memberId);
		String memberAuth = authentication.getAuthorities().toString();
		model.addAttribute("memberAuth",memberAuth);
		Map<String, Object> param = new HashMap<>();
		param.put("cPage", cPage);
		int limit = 5;
		param.put("limit", limit);
		param.put("memberId", memberId);
		List<Inquire> inqList = memberService.selectInquireList(param);
		log.debug("inqList = {}", inqList);
		model.addAttribute("inqList",inqList);
		
		int totalContent = memberService.getTotalInqCntBymemberId(memberId);
		String url = request.getRequestURI();
		String pagebar = HelloSpringUtils.getPagebar(cPage, limit, totalContent, url);
		model.addAttribute("pagebar",pagebar);
		log.debug("model",model);
	}
	//----------------------수진 끝
	//----------------------수아 시작
	@GetMapping("/memberLogin.do")
	public void memberLogin() {
		
	}
	

	@PostMapping("/memberLoginSuccess.do")
	public String memberLoginSuccess(HttpSession session) {
		log.debug("memberLoginSuccess.do 호출!");
		// 로그인 후처리
		String location = "/";
		
		SavedRequest savedRequest = (SavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
		if(savedRequest != null) {
			location = savedRequest.getRedirectUrl();
		}
		log.debug("location = {}", location);
		return "redirect:" + location;
	}

	@GetMapping("/memberIdSearchForm.do")
	public void memberIdSearchForm() {
		
	}
	@Inject
	private JavaMailSender mailSender;
	
	@PostMapping("/memberIdSearchForm.do")
	public String memberIdSearchForm(@RequestParam String email, 
								 @RequestParam String name,
								 RedirectAttributes redirectAttr){
		try {
			MailUtils sendMail = new MailUtils(mailSender);
			Map<String, Object> map = new HashMap<>();
			map.put("email", email);
			map.put("name", name);
			Member member = memberService.findMemberId(map);
			
			if(member == null) {
				redirectAttr.addFlashAttribute("msg", "일치하는 회원정보가 없습니다.");
				return "redirect:/member/memberIdSearchForm.do";
			}
			
			sendMail.setSubject("모농모농 회원 아이디 조회");
			sendMail.setText("<h1>아이디 조회 결과</h1>"
					+ "<br />"+ member.getMemberName()+"님"
							+ "<br />조회하신 아이디는"
							+ "<br /><strong>"+member.getMemberId()+"</strong>입니다."
							+ "<br />감사합니다!"
								);
			sendMail.setFrom("sooappeal31@gmail.com", "모농모농");
			sendMail.setTo(member.getMemberEmail());
			sendMail.send();
			
		} catch (Exception e) {
			log.error("아이디 조회 이메일 전송오류 : " + e.getMessage(), e);
		}
		
		redirectAttr.addFlashAttribute("msg", email+"로 아이디를 보냈습니다. 이메일을 확인해주세요");
		
		return "redirect:/member/memberIdSearchForm.do";
	} 
	
	@GetMapping("/memberPwSearchForm.do")
	public void memberPwSearchForm() {
		
	}
	
	@PostMapping("/memberPwSearchForm.do")
	public String memberPwSearchForm(@RequestParam String email, 
								     @RequestParam String name,
									 RedirectAttributes redirectAttr) throws Exception {
		MailUtils sendMail = new MailUtils(mailSender);
		Map<String, Object> map = new HashMap<>();
		map.put("email", email);
		map.put("name", name);
		Member member = memberService.findMemberId(map);
		if(member == null) {
			redirectAttr.addFlashAttribute("msg", "일치하는 회원정보가 없습니다.");
			return "redirect:/member/memberPwSearchForm.do";
		}
		
		try {
		//임시 비밀번호
		String memberKey = new TempKey().getKey(6,false);
		String memberTempPw = bcryptPasswordEncoder.encode(memberKey);	
		map.put("memberTempPw", memberTempPw);
		int result = memberService.updateTempPw(map);
		
		UserDetails updatedMember = memberSecurityService.loadUserByUsername(member.getMemberId());
		Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
				updatedMember, 
				updatedMember.getPassword(),
				updatedMember.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(newAuthentication);
		
		sendMail.setSubject("모농모농 임시 비밀번호 발급");
		sendMail.setText("<h1>임시 비밀번호</h1>"
		+ "<br />"+ member.getMemberName()+"님"
				+ "<br />임시 비밀번호는"
				+ "<br /><strong>"+memberKey+"</strong>입니다."
				+ "<br />반드시 비밀번호 변경을 해주세요"
				+ "<br />감사합니다!"
					);
		sendMail.setFrom("sooappeal31@gmail.com", "모농모농");
		sendMail.setTo(member.getMemberEmail());
		sendMail.send();
		} catch (Exception e) {
			log.error("아이디 조회 이메일 전송오류 : " + e.getMessage(), e);
		}
		
		redirectAttr.addFlashAttribute("msg", email+"로 임시 비밀번호가 발급됐습니다. 이메일을 확인해주세요");
		
		return "redirect:/member/memberLogin.do";
		} 
	
	//임시 비밀번호 발급 - 일단 6자리로 해놨습니다
	public class TempKey{
			private boolean lowerCheck;
			private int size;
			
			public String getKey(int size, boolean lowerCheck) {
				this.size = size;
				this.lowerCheck = lowerCheck;
				return init();
			}
			
			private String init() {
				Random ran = new Random();
				StringBuffer sb = new StringBuffer();
				int num  = 0;
				do {
					num = ran.nextInt(75) + 48;
					if ((num >= 48 && num <= 57) || (num >= 65 && num <= 90) || (num >= 97 && num <= 122)) {
						sb.append((char) num);
					} else {
						continue;
					}
				} while (sb.length() < size);
				if (lowerCheck) {
					return sb.toString().toLowerCase();
				}
				return sb.toString();
			}
		}
	
	@GetMapping("/memberCheckForm.do")
	public void memberCheck() {
		
	}
	@PostMapping("/memberCheckForm.do")
	public String memberCheck(Authentication authentication,
							  @RequestParam String memberId,
			  				  @RequestParam String memberPassword,
			  				  RedirectAttributes redirectAttr) {
		Member member = (Member) (authentication.getPrincipal());
		String pwd = member.getPassword();
		if(bcryptPasswordEncoder.matches(memberPassword, pwd)) {
			if(member.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SELLER"))) {
				return"redirect:/member/sellerUpdate.do";
			}
			return "redirect:/member/memberUpdate.do";
		}
		log.debug("memberPassword={}",memberPassword);
		log.debug("pwd={}", pwd);
		redirectAttr.addFlashAttribute("msg", "비밀번호를 확인해주세요");
		return "redirect:/member/memberCheckForm.do";
		
	}
		
	@GetMapping("/memberUpdate.do")
	public void memberUpdate() {
		
	}
	@PostMapping("/memberUpdate.do")
	public String memberUpdate(@ModelAttribute Member member, 
							 RedirectAttributes redirectAttr,
							 Model model) {
		int result = memberService.updateMember(member);
		UserDetails updatedMember = memberSecurityService.loadUserByUsername(member.getMemberId());
		
		Authentication updateAthentication = new UsernamePasswordAuthenticationToken(
				updatedMember,
				updatedMember.getPassword(),
				updatedMember.getAuthorities()
				);
		SecurityContextHolder.getContext().setAuthentication(updateAthentication);
		log.debug("member={}", updatedMember);
		redirectAttr.addFlashAttribute("msg", "회원 정보가 수정되었습니다!");
		return "redirect:/member/memberUpdate.do";
		
	}
	@GetMapping("/memberPwUpdate.do")
	public void memberPwUpdate() {
		
	}
	@PostMapping("/memberPwUpdate.do")
	public String memberPwUpdate(@RequestParam String memberId,
								 @RequestParam String oldPassword,
								 @RequestParam String newPassword,
								 RedirectAttributes redirectAttr) {
		try {
				Member member = memberService.selectMemberById(memberId);
				String pwd = member.getPassword();
				if(bcryptPasswordEncoder.matches(oldPassword, pwd)) {
					String encodedPassword = bcryptPasswordEncoder.encode(newPassword);
					Map<String, Object> map = new HashMap<>();
					map.put("memberId", memberId);
					map.put("encodedPassword", encodedPassword);
					int result = memberService.updatePw(map);
					
					UserDetails updatedMember = memberSecurityService.loadUserByUsername(member.getMemberId());
					Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
							updatedMember, 
							updatedMember.getPassword(),
							updatedMember.getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(newAuthentication);
					redirectAttr.addFlashAttribute("msg", "비밀번호가 변경되었습니다.");
				}
				else {
					redirectAttr.addFlashAttribute("msg", "현재 비밀번호를 확인해주세요");
				}
				return "redirect:/member/memberUpdate.do";
		} catch(Exception e) {
			log.error("비밀번호 변경 오류 : " + e.getMessage(), e);
			throw e;
		}
	}

	@GetMapping("/memberSubscribeList.do")
	public void memberSubscribeList(Authentication authentication, Model model) {
		String memberId = authentication.getName();
		Subscription recentSubscription = memberService.selectRecentSubById(memberId); 
		log.debug("recentSubscription={}",recentSubscription);
		if(recentSubscription != null) {
			String pCode = recentSubscription.getSProductCode();
			SubscriptionProduct recentSubProduct = memberService.selectRecentSubProduct(pCode);
			model.addAttribute("recentSubscription", recentSubscription);
			model.addAttribute("recentSubProduct", recentSubProduct);
		}
		
		List<SubscriptionOrder> subList = memberService.selectSubscriptionListById(memberId);
		if(subList != null) {
			log.debug("subList={}",subList);
			model.addAttribute("subList", subList);
		}
	}	
		
	@GetMapping("/memberOrderList.do")
	public void memberOrderList(Authentication authentication, Model model) {
		String memberId = authentication.getName();
		Subscription recentSubscription = memberService.selectRecentSubById(memberId); 
		log.debug("recentSubscription={}",recentSubscription);
		if(recentSubscription != null) {
			String pCode = recentSubscription.getSProductCode();
			SubscriptionProduct recentSubProduct = memberService.selectRecentSubProduct(pCode);
			model.addAttribute("recentSubscription", recentSubscription);
			model.addAttribute("recentSubProduct", recentSubProduct);
		}	
	}
	
	@GetMapping("/memberReviewList.do")
	public void memberReviewList(Authentication authentication, Model model) {
		String memberId = authentication.getName();
		Subscription recentSubscription = memberService.selectRecentSubById(memberId); 
		log.debug("recentSubscription={}",recentSubscription);
		if(recentSubscription != null) {
			String pCode = recentSubscription.getSProductCode();
			SubscriptionProduct recentSubProduct = memberService.selectRecentSubProduct(pCode);
			model.addAttribute("recentSubscription", recentSubscription);
			model.addAttribute("recentSubProduct", recentSubProduct);
		}	
	}
	
	@GetMapping("/memberDirectInquire.do")
	public void memberDirectInquire(Authentication authentication, Model model){
		String memberId = authentication.getName();
		Subscription recentSubscription = memberService.selectRecentSubById(memberId); 
		log.debug("recentSubscription={}",recentSubscription);
		if(recentSubscription != null) {
			String pCode = recentSubscription.getSProductCode();
			SubscriptionProduct recentSubProduct = memberService.selectRecentSubProduct(pCode);
			model.addAttribute("recentSubscription", recentSubscription);
			model.addAttribute("recentSubProduct", recentSubProduct);
		}	
	}
//	
//	@GetMapping("/memberSubscriptionList.do")
//	public void memberInquireList(Authentication authentication, Model model) {
//		String memberId = authentication.getName();
//		Subscription recentSubscription = memberService.selectRecentSubById(memberId); 
//		log.debug("recentSubscription={}",recentSubscription);
//		if(recentSubscription != null) {
//			String pCode = recentSubscription.getSProductCode();
//			SubscriptionProduct recentSubProduct = memberService.selectRecentSubProduct(pCode);
//			model.addAttribute("recentSubscription", recentSubscription);
//			model.addAttribute("recentSubProduct", recentSubProduct);
//		}
//	}
//	
	@Autowired
	private SubscribeService subscribeService;
	
	@GetMapping("/memberSubscribeOrder.do")
	public void memberSubscribeOrder(Authentication authentication, Model model) {
		List<SubscriptionProduct> subscriptionProduct = subscribeService.getSubscriptionProduct();
		log.debug("subscriptionProduct = {}", subscriptionProduct);

		List<Vegetables> vegetables = subscribeService.getVegetables();
		log.debug("vegetables = {}", vegetables);

		model.addAttribute("subscriptionProduct", subscriptionProduct);
		model.addAttribute("vegetables", vegetables);
		
		String memberId = authentication.getName();
		Subscription recentSubscription = memberService.selectRecentSubById(memberId); 
		String pCode = recentSubscription.getSProductCode();
		SubscriptionProduct recentSubProduct = memberService.selectRecentSubProduct(pCode);
		model.addAttribute("recentSubscription", recentSubscription);
		model.addAttribute("recentSubProduct", recentSubProduct);
		
	}
	
	@PostMapping("/memberSubscribeOrderUpdate.do")
	public String memberSubscribeOrderUpdate(
			Subscription subscription,
			RedirectAttributes redirectAttr) {
		
		int result = memberService.updateSubscribeOrder(subscription);
		redirectAttr.addFlashAttribute("msg", "구독 수정이 완료되었습니다. :)");
		return "redirect:/member/memberSubscribeList.do";
	}
	
	@GetMapping("/memberSubscribeDetail.do")
		public void memberSubscribeDetail(@RequestParam String sOrderNo, Model model) {
			log.debug("sOrderNo={}",sOrderNo);
			SubscriptionOrder subOrder = memberService.selectOneSubscriptionOrder(sOrderNo);
			SubscriptionProduct subProduct = memberService.selectRecentSubProduct(subOrder.getSoProductCode());
			log.debug("subOrder={}",subOrder);
			log.debug("subProduct={}",subProduct);
			model.addAttribute("subOrder",subOrder);
			model.addAttribute("subProduct", subProduct);
			
		}
	
	
	//----------------------수아 끝
}
