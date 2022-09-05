﻿<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<fmt:requestEncoding value="utf-8"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
	<jsp:param name="title" value="모농모농-상품 상세 페이지" />
</jsp:include>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/direct.css" />
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.3.1/css/all.css" integrity="sha384-mzrmE5qonljUremFsqc01SB46JvROS7bZs3IO2EmfFsd15uHvIt+Y8vEf7N7fWAU" crossorigin="anonymous">

<main class="main-container">
  <div class="slider-container">
    <div class="slider-1">
        <div class="slides">
          <div class="active" style="background-image:url(/sample/image/flower1.PNG);background-size: 100%;"></div>
          <div style="background-image:url(https://images.prismic.io/stayfolio-production/038914a1-9b23-4f26-9b10-84669d9a1339_banner.jpg?auto=compress,format);"></div>
          <div style="background-image:url(https://images.prismic.io/stayfolio-production/586abdef-7163-4629-9c39-6d7a627ae10e_01_banner_picture.jpg?auto=compress,format);"></div>
          <div style="background-image:url(https://images.prismic.io/stayfolio-production/3b4bf417-3c41-4d05-b62c-f02a293ba115_banner.jpg?auto=compress,format);"></div>
        </div>
        <div class="page-btns">
            <div class="active" style="background-image:url(/sample/image/flower1.PNG);background-size: cover;background-position: center;"> </div>
            <div style="background-image:url(https://images.prismic.io/stayfolio-production/038914a1-9b23-4f26-9b10-84669d9a1339_banner.jpg?auto=compress,format);background-size: cover;background-position: center;"> </div>
            <div style="background-image:url(https://images.prismic.io/stayfolio-production/586abdef-7163-4629-9c39-6d7a627ae10e_01_banner_picture.jpg?auto=compress,format);background-size: cover;background-position: center;"> </div>
            <div style="background-image:url(https://images.prismic.io/stayfolio-production/3b4bf417-3c41-4d05-b62c-f02a293ba115_banner.jpg?auto=compress,format);background-size: cover;background-position: center;"> </div>
        </div>
        <div class="side-btns">
            <div>
                <span><i class="fas fa-angle-left"></i></span>
            </div>
            <div>
                <span><i class="fas fa-angle-right"></i></span>
            </div>
        </div>
    </div>
  </div>
  <div class="pInfo-container">
    <span>유기농 블루베리</span><br>
    <span>29,900원</span><span>여기에 별</span><span>별점</span>
    <div style="border: 0.5px solid black; background-color: black;"></div>
    <span>배송비</span><span></span><br>
    <span>판매자</span><span></span>
    <div style="border: 0.5px solid black; background-color: black;"></div>
  </div>
  <hr>
  <div class="dropdown">
    <button class="dropbtn" onclick="dropdown()">
      <span class="dropbtn_content" onclick="dropdown()">옵션 선택</span>
      <span class="dropbtn_click" style="font-size : 16px; color : #3b3b3b; float:right;">▼</span>
    </button>
    <div class="dropdown-content">
      <div class="fastfood" onclick="showMenu(this.innerText)">옵션 1</div>
      <div class="fastfood" onclick="showMenu(this.innerText)">옵션 2</div>
      <div class="fastfood" onclick="showMenu(this.innerText)">옵션 3</div>
      <div class="fastfood" onclick="showMenu(this.innerText)">옵션 4</div>
      <div class="fastfood" onclick="showMenu(this.innerText)">옵션 5</div>
    </div>
  </div>
</main>
<script>
//기존 버튼형 슬라이더
$('.slider-1 > .page-btns > div').click(function(){
    var $this = $(this);
    var index = $this.index();
    
    $this.addClass('active');
    $this.siblings('.active').removeClass('active');
    
    var $slider = $this.parent().parent();
    
    var $current = $slider.find(' > .slides > div.active');
    
    var $post = $slider.find(' > .slides > div').eq(index);
    
    $current.removeClass('active');
    $post.addClass('active');
});

// 좌/우 버튼 추가 슬라이더
$('.slider-1 > .side-btns > div').click(function(){
    var $this = $(this);
    var $slider = $this.closest('.slider-1');
    
    var index = $this.index();
    var isLeft = index == 0;
    
    var $current = $slider.find(' > .page-btns > div.active');
    var $post;
    
    if ( isLeft ){
        $post = $current.prev();
    }
    else {
        $post = $current.next();
    };
    
    if ( $post.length == 0 ){
        if ( isLeft ){
            $post = $slider.find(' > .page-btns > div:last-child');
        }
        else {
            $post = $slider.find(' > .page-btns > div:first-child');
        }
    };
    
    $post.click();
});

//setInterval(function(){
//    $('.slider-1 > .side-btns > div').eq(1).click();
//}, 3000);

</script>
<script>
  window.onload=()=>{
	  
      document.getElementsByClassName('fastfood').onclick = ()=>{
        showMenu(value);
      };
      dropdown = () => {
        var v = document.querySelector('.dropdown-content');
        var dropbtn = document.querySelector('.dropbtn')
        v.classList.toggle('show');
        dropbtn.style.borderColor = 'rgb(94, 94, 94)';
      }

      showMenu=(value)=>{
        var dropbtn_content = document.querySelector('.dropbtn_content');
        var dropbtn = document.querySelector('.dropbtn');

        /* dropbtn_content.innerText = value; */
        dropbtn_content.style.color = '#252525';
        dropbtn.style.borderColor = 'black';
      }
    }
    window.onclick= (e)=>{
      if((!e.target.matches('.dropbtn')) && (!e.target.matches('.dropbtn_content'))){
        var dropdowns = document.getElementsByClassName("dropdown-content");

        var i;
        for (i = 0; i < dropdowns.length; i++) {
          var openDropdown = dropdowns[i];
          if (openDropdown.classList.contains('show')) {
            openDropdown.classList.remove('show');
          }
        }
      }
    }
</script>
<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>