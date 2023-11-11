package kr.or.ddit.service.member.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import kr.or.ddit.ServiceResult;
import kr.or.ddit.mapper.member.MyCouponMapper;
import kr.or.ddit.service.member.IMyCouponService;
import kr.or.ddit.vo.member.MyCouponVO;

@Service
public class MyCouponServiceImpl implements IMyCouponService {

	@Inject
	private MyCouponMapper mycouponMapper;
	
	/**
	 * 나의 쿠폰함 내역 조회 서비스 로직
	 * 
	 * @param memId 회원아이디
	 * @return List<MyCouponVO> 타입의 객체 리턴
	 */
	@Override
	public List<MyCouponVO> myCouponList(String memId) {
		return mycouponMapper.myCouponList(memId);
	}

	/**
	 * 회원 쿠폰 생성 서비스 로직
	 * 
	 * @param mycouponVO 객체
	 * @return result
	 */
	@Override
	public ServiceResult create(MyCouponVO mycouponVO) {
		
		ServiceResult result = null;
		
		MyCouponVO mycoupon = mycouponMapper.mycoupon(mycouponVO);
		
		if(mycoupon == null) {
			
			int status = mycouponMapper.mycouponcreate(mycouponVO);
			
			if(status > 0) {
				result = ServiceResult.OK;
			}else {
				result = ServiceResult.FAILED;
			}
			
		}else {
			result = ServiceResult.FAILED;
		}
		return result;
	}

}
