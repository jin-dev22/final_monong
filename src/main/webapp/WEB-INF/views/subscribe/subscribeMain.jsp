<%@page import="com.kh.monong.member.model.dto.MemberEntity"%>
<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page import="com.kh.monong.member.model.dto.Member"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<fmt:requestEncoding value="utf-8" />
<jsp:include page="/WEB-INF/views/common/header.jsp">
	<jsp:param name="title" value="모농모농"></jsp:param>
</jsp:include>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.9.1/font/bootstrap-icons.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/subscribe/sMain.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/subscribe/sReview.css">

	<h1>모농모농 정기구독 이용안내</h1>
	<div class="s-info-step step1">
		<div class="s-info-step-title">
			<span class="badge rounded-pill text-dark">Step 1</span> <span>구독신청</span>
			<p>나에게 딱 맞는 플랜으로 신청하세요!</p>
		</div>
		<div class="s-info-step1">
			<div class="s-info-step1-product">
				<p>상품</p>
				<c:forEach items="${subscriptionProduct}" var="product" varStatus="vs">
					<div class="" data-sproduct="${product.SProductCode}" data-index="${vs.index}">
						<div class="s-info-step1-img">
							<img src="${pageContext.request.contextPath}/resources/images/subscribe/${product.SProductName}.jpg" alt="${product.SProductName}" >
						</div>
						<span class="s-product-name">${product.SProductName}</span>
		                <span class="s-product-price">
		                	<fmt:formatNumber value="${product.SProductPrice}" pattern="#,###원" />
		                </span>
		                <span class="s-product-info">${product.SProductInfo}용</span>
					</div>
				</c:forEach>
			</div>
			<div class="mean-nothing-div"></div>
			<div class="s-info-step1-cycle">
				<p>배송주기</p>
				<div class="mean-nothing-box">
					<span>1주</span> <span>#프로요리사</span>
				</div>
				<div class="mean-nothing-box">
					<span>2주</span> <span>#해먹는 재미</span>
				</div>
				<div class="mean-nothing-box">
					<span>3주</span> <span>#요리초보</span>
				</div>
			</div>
			<div class="s-info-step1-exclude">
				<p>제외 채소 선택</p>
				<span>제외하고 싶은 채소를 최대 5개까지 선택하실 수 있습니다.</span><br /> <span>배송되는
					주간의 다른 채소로 대채해 보내드립니다.</span>
			</div>
	
		</div>
	</div>
	<div class="s-info-step step2">
		<div class="s-info-step-title">
			<span class="badge rounded-pill text-dark">Step 2</span> <span>배송</span>
			<p>설레는 금요일~ 신선한 채소를 배송받아요!</p>
		</div>
		<div class="step2-wrapper">
			<div class="s-info-step2-box">
				<p>월</p>
				<i class="bi bi-megaphone-fill"></i>
				<p>주간 채소 공지</p>
			</div>
			<div class="step2-arrow">
				<i class="bi bi-arrow-right-short"></i>
			</div>
			<div class="s-info-step2-box">
				<p>수</p>
				<i class="bi bi-credit-card-fill"></i>
				<p>결제</p>
			</div>
			<div class="step2-arrow">
				<i class="bi bi-arrow-right-short"></i>
			</div>
			<div class="s-info-step2-box">
				<p>금</p>
				<i class="bi bi-box2-fill"></i>
				<p>배송 완료</p>
			</div>
		</div>
		<div class="step2-footer">
			<span>&#128504; 배송 미루기</span> <span>&#128504; 구독 플랜 수정하기</span><br />
			<span>※ 매주 화요일까지 변경 가능</span><br />
			<sec:authorize access="isAnonymous()"> 
				<button type="button" id="gotoLogin" class="btn btn-EA5C2B">구독하기</button>
			</sec:authorize>
			<sec:authorize access="isAuthenticated()">
		    	<c:if test="${isSubscribe eq 'Y'}">
					<button type="button" id="gotoPlan" class="btn btn-EA5C2B" disabled>구독하기</button>
				</c:if>
				<c:if test="${isSubscribe ne 'Y'}">
					<button type="button" id="gotoPlan" class="btn btn-EA5C2B">구독하기</button>
				</c:if>
			</sec:authorize>
		</div>
	</div>

<h1>후기</h1>
<sec:authentication property="principal" var="loginMember" scope="page"/>
<!-- Modal -->
<div class="modal" id="myModal" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
      	<div class="modal-img"></div>
      	<div class="modal-s-review-container">
	     	<div class="modal-s-review-star-times">
				<span class="modal-s-review-star">
				    <span class="modal-s-review-star-filled">★★★★★</span>
				    <span class="modal-s-review-unfilled">★★★★★</span>
			    </span>
			    <span class="modal-s-times"></span>
		    </div>
		    <div class="modal-s-review-content"></div>
		    <div class="modal-s-review-member-id"></div>
		    <div class="modal-s-review-recommend-wrapper">
	    	<div class="modal-s-review-recommend-info">
	    		<span class="modal-s-review-recommend-num"></span>
	    		<span class="modal-s-review-recommend-content">명의 회원이 추천한 리뷰입니다.</span>
	    	</div>
	    	<sec:authorize access="isAuthenticated()">
		    	<c:if test="${loginMember.memberId ne null}">
					<div class="modal-s-review-recommend" onclick="sReviewRecommend();">
			    		<input type="button" class="btn-s-review-recommend" value="👍추천하기" />
			    	</div>
			    </c:if>
	    	</sec:authorize>
		    </div>
	    </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>


<script>
// 선아 비로그인 - 구독하기 버튼 클릭 시 로그인페이지로 이동
document.querySelector("#gotoLogin").addEventListener('click', () => {
	alert("로그인 후 이용해주세요!");
	location.href = `${pageContext.request.contextPath}/member/memberLogin.do`;
});


const sReviewRecommend = () => {
	const recommendNum = document.querySelector(".modal-s-review-recommend-num");	
	console.log(recommendNum.dataset.sReviewNo);
	
	const sReviewNo = recommendNum.dataset.sReviewNo;
	
	$.ajax({
		url : "${pageContext.request.contextPath}/subscribe/subscribeReviewRecommend.do",
		data: {sReviewNo},
		method : "POST",
		beforeSend : function(xhr){  
			            xhr.setRequestHeader("${_csrf.headerName}", "${_csrf.token}");
		       		 },
		success(result){
    		const recommendNum = document.querySelector(".modal-s-review-recommend-num");
			recommendNum.innerHTML = Number(recommendNum.innerHTML) + 1;
		},
		error : console.log
	});
};

</script>
<p>모농모농의 정기구독을 이용하신 고객님들의 후기입니다.</p>

 <div class="s-review-statistics">
        <div class="s-review-star">전체 만족도: ${sReviewStarAvg}</div>
        <div class="s-review-num">전체 후기 수: ${totalContent}</div>
    </div>
<div class="s-reviews-wrapper"></div>

<nav class="s-review-page-bar">
	${pagebar}
</nav>

<script>
window.onload = () => {
	$.ajax({
		url : "${pageContext.request.contextPath}/subscribe/subscribeReviewList.do",
		method : "GET",
		success(result){
			console.log('result', result);
			console.log('result', result['sReviewList']);
			const reviews = result['sReviewList'];
			console.log('result', result['pagebar']);
			const pagebar = result['pagebar'];

			let html = '';
	
			reviews.forEach((review, index) => {
				const sAttach = review.sattachments;
				console.log('sAttach', sAttach);
				
				const {memberId, sreviewContent, sreviewCreatedAt, sreviewStar, stimes} = review;
				
				html += `
				<div class="s-review-wrapper" onclick="reviewDetail(this,'\${review.sreviewNo}');" data-toggle="modal" data-target="#myModal">`;
				if(!sAttach[0]){
					console.log('이미지 없음');
					html += `
						<div class="s-review-container no-img">`;
				}
				else{
					console.log('이미지 있음');
					/* 구독 작성 기능 완료 후 이미지 경로 수정 진행 */
					html += `
						<div>
							<img src="${pageContext.request.contextPath}/resources/images/subscribe/싱글.jpg">
						</div>
						<div class="s-review-container with-img">`;
				}
				
				html += `
					<div class="s-review-star-times">
						<span class="s-review-star">
						    <span class="s-review-star-filled" style="width:\${sreviewStar*20}%">★★★★★</span>
						    <span class="s-review-unfilled">★★★★★</span>
					    </span>
					    <span class="s-times">\${stimes}회차 구독회원</span>
					</div>`;
					
			    if(!sAttach[0]){
			    	html += `
			    		<p class="s-review-content">\${sreviewContent}</p>`;
			    }
				else{
					html += `
						<p class="s-review-content">\${sreviewContent.length > 40 ? sreviewContent.substr(0, 40) + '...': sreviewContent}</p>`;
			    }
			    html += `
			   		</div>
					<div class="s-review-member-id">\${memberId}</div>
				</div>`;
			
			});
			document.querySelector(".s-reviews-wrapper").innerHTML = html;
			document.querySelector(".s-review-page-bar").innerHTML = pagebar;
				
		},
		error : console.log
	});
	
}; 

const reviewDetail = (obj, sReviewNo) =>{
	console.log('obj: ', obj, 'sReviewNo: ', sReviewNo);
	
	$.ajax({
		url : "${pageContext.request.contextPath}/subscribe/subscribeReviewDetail.do",
		data: {sReviewNo},
		method : "GET",
		success(review){
			console.log('review(modal)', review);
			const sAttach = review.sattachments;
			console.log('sAttach',sAttach);

			const {memberId, sreviewContent, sreviewCreatedAt, sreviewRecommendNum, sreviewStar, stimes} = review;
			
		 	const modalImg = document.querySelector(".modal-img");
			if(sAttach[0]){
				let html;
					/* 구독 작성 기능 완료 후 이미지 경로 수정 진행 */
					html = `
				      	<img src="${pageContext.request.contextPath}/resources/images/subscribe/싱글.jpg" width="300px" class="modal-s-review-img"/>
				      `;
			      modalImg.innerHTML = html;
			}
			else{
				// 다른 게시물 클릭 시 넣어놓았던 이미지 html 제거
				modalImg.innerHTML = '';
			}
			const moTimes = document.querySelector(".modal-s-times");
			moTimes.innerHTML = `\${stimes}회차 구독`;
			
			const moStar = document.querySelector(".modal-s-review-star-filled");
			moStar.style.width=`\${sreviewStar*20}%`;
			
			const moContent = document.querySelector(".modal-s-review-content");
			moContent.innerHTML = `\${sreviewContent}`;
			
			const moMemberId = document.querySelector(".modal-s-review-member-id");
			moMemberId.innerHTML = `\${memberId}`;
			
			const moRecommendNum = document.querySelector(".modal-s-review-recommend-num");
			moRecommendNum.innerHTML = `\${sreviewRecommendNum}`;
			moRecommendNum.setAttribute('data-s-review-no', sReviewNo);
	
		},
		error : console.log
	});
	
	setTimeout(showModal, 100);
}

const showModal = () => {
	var myModal = new bootstrap.Modal(document.getElementById('myModal'), 'show');
	myModal.show();
}
document.querySelector("#gotoPlan").addEventListener('click', () => {
	location.href = `${pageContext.request.contextPath}/subscribe/subscribePlan.do`;
});
</script>
<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>