package kr.or.ddit.controller.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.ServiceResult;
import kr.or.ddit.service.member.IMemberReviewService;
import kr.or.ddit.service.member.IMyCouponService;
import kr.or.ddit.service.owner.IFrcsMenuService;
import kr.or.ddit.service.owner.ISeatService;
import kr.or.ddit.vo.AlarmVO;
import kr.or.ddit.vo.member.MyCouponVO;
import kr.or.ddit.vo.member.ResVO;
import kr.or.ddit.vo.member.ReviewVO;
import kr.or.ddit.vo.owner.FrcsMenuVO;
import kr.or.ddit.vo.owner.FrcsReviewVO;
import kr.or.ddit.vo.owner.FrcsSeatVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/elly")
public class ResMemberController {

	@Inject
	private IFrcsMenuService menuService;
	
	@Inject
	private ISeatService seatService;
	
	@Inject
	private IMyCouponService mycouponService;
	
	@Inject
	private IMemberReviewService memberreviewService;
	
	@PreAuthorize("hasRole('ROLE_MEMBER')")
	@RequestMapping(value = "/store/res.do", method = RequestMethod.GET)
	public String mapForm(String memId, String frcsId, Model model) {
		
		// 메뉴 리스트 조회 기능
		List<FrcsMenuVO> frcsMenuList = menuService.resfrcsMenuList(frcsId);
		model.addAttribute("menulist", frcsMenuList);
		
		// 좌석 리스트 조회 기능
		List<FrcsSeatVO> frcsSeatList = seatService.frcsSeatList(frcsId);
		model.addAttribute("seatlist", frcsSeatList);
		model.addAttribute("frcsId", frcsId);
		
		// 쿠폰 리스트 조회 기능
		List<MyCouponVO> couponList = mycouponService.myCouponList(memId);
		int mycouponCnt = couponList.size();
		model.addAttribute("couponList", couponList);
		model.addAttribute("mycouponCnt", mycouponCnt);
		
		
		return "mainhome/store/resForm";
	}
	
	@ResponseBody
	@RequestMapping(value = "/store/res.do", method = RequestMethod.POST)
	public ResponseEntity<ServiceResult> frcsOrder(@RequestBody ResVO resVO) {

		ServiceResult result = menuService.resRegister(resVO);
	    
		return new ResponseEntity<ServiceResult>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/store/review.do" , method = RequestMethod.POST)
	public String reviewSuccess(
			RedirectAttributes ra,
			AlarmVO alarmVO, ReviewVO reviewVO, Model model
			) {
		
		String goPage = "";
		
		Map<String, String> errors = new HashMap<String, String>();
		
		if(StringUtils.isBlank(reviewVO.getReviewContent())) {
			errors.put("reviewText", "리뷰를 입력해주세요");
		}
		if(errors.size() > 0) {
			model.addAttribute("errors", errors);
			model.addAttribute("review", reviewVO);
			goPage = "";
		}else {
			ServiceResult result = memberreviewService.create(reviewVO, alarmVO);
			if(result.equals(ServiceResult.OK)) {
				ra.addFlashAttribute("message", "리뷰등록이 완료되였습니다!");
				goPage = "redirect:/elly/mypage.do?memId=" + reviewVO.getMemId();
			}else {
				model.addAttribute("bodyText", "register-page");
				model.addAttribute("message", "서버 에러, 다시 시도해주세요!");
				model.addAttribute("review", reviewVO);
				goPage = "";
			}
			
		}
		
		return goPage;
	}
	
//	@PreAuthorize("hasRole('ROLE_MEMBER')")
//	@ResponseBody
//	@RequestMapping(value = "/store/mycoupon.do")
//	public ResponseEntity<List<MyCouponVO>> myCouponList(@RequestBody Map <String,String> map){
//		
//		List<MyCouponVO> result = new ArrayList<>();
//		
//		String memId = map.get("memId").toString();
//
//		result = mycouponService.myCouponList(memId);
//		
//		return new ResponseEntity<List<MyCouponVO>>(result, HttpStatus.OK);
//	}
	
	
	//리뷰관련 알림
	@ResponseBody
	@RequestMapping(value = "/selectAlarmMemberReview.do", method=  RequestMethod.POST, produces = "application/json;charset=utf-8")
	public List<AlarmVO> selectAlarm(@RequestBody Map<String, String> map) {
		
		String memId = map.get("memId").toString();
		
		List<AlarmVO> alarm = memberreviewService.selectMemberReviewAlarmList(memId);
		return alarm;
		
	}
	
	//리뷰확인시 update 처리로 db에 'Y'로 변경
	@RequestMapping(value = "/updateAlarmMemberReview.do")
	public String updateAlarm(int alarmNo) {
		log.info("updateAlarm에서 넘어오는지 확인 >>>>>>>>>>>>>>>>>> "+alarmNo);
		memberreviewService.updateMemberReviewAlarm(alarmNo);
		return "redirect:/owner/home.do";
	}
	
	
	//1개 삭제
	@ResponseBody
	@RequestMapping(value = "/deleteAlarm.do", method = RequestMethod.POST)
		public ResponseEntity<ServiceResult> deleteAlarm(@RequestBody Map<String, String> map) {

		int alarmNo = Integer.parseInt(map.get("alarmNo").toString());
			
		ServiceResult result = memberreviewService.deleteMemberReviewAlarm(alarmNo);
		    
			return new ResponseEntity<ServiceResult>(result, HttpStatus.OK);
	}
	
	//전체 삭제 + 추후 건의합니다 조인필요
	@ResponseBody
	@RequestMapping(value = "/deleteclearAllAlarm.do", method = RequestMethod.POST)
		public ResponseEntity<ServiceResult> deleteclearAllAlarm(@RequestBody Map<String, String> map) {
		
		String ansId = map.get("ansId").toString();
			
		ServiceResult result = memberreviewService.deleteclearAllAlarm(ansId);
		    
			return new ResponseEntity<ServiceResult>(result, HttpStatus.OK);
	}

		
}
