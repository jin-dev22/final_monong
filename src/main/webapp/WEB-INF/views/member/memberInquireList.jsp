<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<sec:authorize access="isAuthenticated() && !hasRole('ROLE_SELLER')">
	<jsp:include page="/WEB-INF/views/member/memberMyPage.jsp"></jsp:include>
</sec:authorize>
<sec:authorize access="hasRole('ROLE_SELLER')">
	<jsp:include page="/WEB-INF/views/member/sellerMyPage.jsp"></jsp:include>
</sec:authorize>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/inquire/inquireAccordion.css" />
<div id="member-inquire-container">
	<c:if test="${empty inqList}">
		<div class="mx-auto mt-5 text-center">
			<h3>문의하신 내역이 없어요 :(</h3>
			<span>아래 '문의하기' 에서 관리자에게 문의하실 수 있습니다</span>
		</div>
	</c:if>
	<c:if test="${not empty inqList}">
		<div class="inquireList-header">
			<span class="inq-header-item">제목</span>
			<span class="inq-header-item">작성일</span>
			<span class="inq-header-item">답변</span>
		</div>
		<div class="accordion" id="inqList-accordion">
			<c:forEach items="${inqList}" var="inq" varStatus="vs">
				<div class="accordion-item">
				    <div class="accordion-header" id="heading${vs.count}">
				      <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${vs.count}" aria-expanded="true" aria-controls="collapse${vs.count}">
				        <div class="inq-title-align">
					        <span class="inq-title-item">${inq.inquireTitle}</span>
							<span class="inq-title-item">${inq.inquireCreatedAt}</span>
							<span class="inq-title-item">${inq.hasAnswer eq 'Y' ? '답변완료' : '답변 대기중'}</span>
				        </div>
				      </button>
				    </div>
					<div id="collapse${vs.count}" class="accordion-collapse collapse" aria-labelledby="heading${vs.count}" data-bs-parent="#inqList-accordion">
				      <div class="accordion-body">
				        <div class="inq-content-container inq-form-align">
				        	<label for="memberId" class="inq-memberId">🥕${inq.memberId} :&nbsp;</label>
				        	<textarea name="memberId" id="memberId" cols="80" rows="10" style="resize: none;" readOnly>${inq.inquireContent}</textarea>
				        </div>
				        <br />
				        <div class="inq-answer px-4">
				    		<c:if test="${not empty inq.inquireAnswer.inquireANo}">
				        		<div class="inq-form-align">
					        		<div class="inq-form-align">
							        	<label for="inquireAContent">🍃관리자 :&nbsp;</label>
							        	<textarea name="inquireAContent" id="inquireAContent" cols="80" rows="10" style="resize: none;" readOnly>${inq.inquireAnswer.inquireAContent}</textarea>
					        		</div>
									<div class="px-2 d-flex flex-column justify-content-space-between">
							        	<span>${inq.inquireAnswer.inquireAnsweredAt}</span>
							        	<input type="hidden" name="inquireNo" value="${inq.inquireNo}"/>
							        	<sec:csrfInput />
									</div>
				        		</div>
				    		</c:if>
				        </div>
				      </div>
				    </div>			
				</div>
			</c:forEach>
		</div>
		<nav>
			${pagebar}
		</nav>
	</c:if>
</div>
<script>	
	$("#lnik-inqList").css("color","EA5C2B")
</script>
<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>