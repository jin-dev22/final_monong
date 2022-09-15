package com.kh.monong.member.model.service;

import java.util.List;
import java.util.Map;

import com.kh.monong.direct.model.dto.DirectProduct;
import com.kh.monong.member.model.dto.Member;
import com.kh.monong.member.model.dto.Seller;
import com.kh.monong.member.model.dto.SellerInfo;
import com.kh.monong.member.model.dto.SellerInfoAttachment;
import com.kh.monong.subscribe.model.dto.SubscriptionOrderEx;
import com.kh.monong.subscribe.model.dto.SubscriptionProduct;

public interface MemberService {
//-----------수진시작
	Member selectMemberById(String memberId);
	
	Member selectMemberByEmail(String email);
	
	int insertMember(Map<String, Object> memberAuthMap, Member member);
	
	int insertMemberAuth(Map<String, Object> memberAuthMap);
	
	int insertEmailIdentify(Map<String, Object> map);
	
	String getEmailKey(String email);
	
	
	int insertSeller(Map<String, Object> memberAuthMap, Seller seller);

	int insertSellerInfo(SellerInfo sellerInfo);
	
	int insertSellerInfoAttachment(SellerInfoAttachment attachment);
	
	SellerInfo selectSellerInfo(String memberId);
	
	List<DirectProduct> selectDirectListBySellerId(Map<String, Object> param);
	
	/**
	 * 판매자 판매상품 개수
	 */
	int getTotalProdCntBySeller(Map<String, Object> param);
	
	List<Map<String, Object>> selectOrderListByProdNo(Map<String, Object> param);
	
	int getTotalOrderCntByProdNo(Map<String, Object> param);

	String selectProdNameByNo(String prodNo);

	int updateDOrderStatus(Map<String, Object> param);
	
	SellerInfoAttachment selectSellerInfoAttachment(int no);
//-----------수진 끝
//-----------수아 시작
	Member findMemberId(Map<String, Object> map);

	int updateTempPw(Map<String, Object> map);
	
	int updateMember(Member member);

	int updatePw(Map<String, Object> map);
	
	List<Member> findAllMember(Map<String, Integer> param);

	int getTotalMember();

	List<Seller> findAllSeller(Map<String, Integer> param);

	int getTotalSeller();

	int getTotalWaitSeller();

	List<Seller> findWaitSeller(Map<String, Integer> param);

	SellerInfoAttachment selectSellerAttach(int no);

	int updateSellerStatus(String id);

	int getTotalSellerEnrollByMonth();
	
	SubscriptionOrderEx selectSubById(String memberId);

	SubscriptionOrderEx selectRecentSubById(String memberId);

	SubscriptionProduct selectRecentSubProduct(String pCode);

	int deleteSeller(String memberId);
//-----------수아 끝


	

	
}
