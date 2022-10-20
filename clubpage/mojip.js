$(function(){ 
    $(".modalmojip_on").click(function(){
      $(".modalmojip").fadeIn();
    });
    
    $(".modalmojip_out").click(function(){
      $(".modalmojip").fadeOut();
    });



    $(".mojip_summit").click(function(){
      $(".modalmojip").fadeOut();

      // const mojip_name = $(".mojip_name").val();
      // const mojip_content = $(".mojip_content").val();
      
      const mojippostContainer = document.querySelector("#mojipPost")

      const postTimes = new Date();
      const postYear = postTimes.getFullYear();
      const postMonth = postTimes.getMonth()+1;
      const postDay = postTimes.getDate();

      const postTime = postYear + "년 "+ postMonth +"월 " + postDay+"일";

      const mojip_name = document.querySelector(".mojip_name");
      const mojip_content = document.querySelector(".mojip_content");

      
      const mojipPost = document.querySelector("#mojipPost");
      const nameli = document.createElement("li");

      const h3tag = document.createElement("h3"); 
      const mojip_name_value = document.createTextNode(mojip_name.value);

      h3tag.appendChild(mojip_name_value);
      nameli.append(h3tag);
      // nameli.append(mojip_name.value);
      nameli.append(postTime)
      mojippostContainer.append(nameli);


      // 입력했던 값 초기화
      mojip_name.value = ""
      mojip_content.value = ""
    });
  });



  

  // const postnameInput = clubmodal_form.elements.postnameInput;
  // const postcontentTextarea = clubmodal_form.elements.postcontentTextarea;


  // const instaForm = document.querySelector("#instaForm");

    // // 폼태그의 요소의 input태그들을 name값을 이용해 변수에 담음 
    // const usernameInput = instaForm.elements.username;
    // const commentInput = instaForm.elements.comment;