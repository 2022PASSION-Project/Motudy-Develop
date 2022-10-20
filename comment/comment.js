const instaForm = document.querySelector("#instaForm");
const commentsContainer = document.querySelector("#comments");


// form요소 안에 있는 button태그는 클릭이벤트 발생시 submit동작이 기본적으로 발생함
instaForm.addEventListener("submit", function cccc(e) {
  // 이벤트 객체의 preventDefault 메소드를 실행하면 기본 동작이 취소된다(페이지 새로고침 x)
  e.preventDefault(); 
  
  // 폼태그의 요소의 input태그들을 name값을 이용해 변수에 담음 
  const usernameInput = instaForm.elements.username;
  const commentInput = instaForm.elements.comment;



  //입력한 값을 불러와 인자로 줌
  addComment(usernameInput.value, commentInput.value);

  //댓글입력자, 댓글내용 초기화
  usernameInput.value = "";
  commentInput.value = "";
});

//화살표 함수 addComment선언 
const addComment = (username, comment) => {
const newComment = document.createElement("li");
const bTag = document.createElement("b");
const nextline = document.createElement('br');
  const postTimes = new Date();
  const postYear = postTimes.getFullYear();
  const postMonth = postTimes.getMonth()+1;
  const postDay = postTimes.getDate();
  const postTime = postYear + "년 "+ postMonth +"월 " + postDay+"일";

bTag.append(username);
newComment.append(bTag);
newComment.append(` - `+comment);
newComment.append(nextline);
newComment.append(postTime);
commentsContainer.append(newComment);
};





/*
const usernameInput = document.querySelectorAll("input")[0]; // querySelectorAll이용해 조회
const commentInput = document.querySelectorAll("input")[1];
console.log(usernameInput.value, commentInput.value); // 값 읽어 오는지 확인
*/
/*
// #instaForm의 요소중 name이 username, comment인 것의 value 값 가져옴
const username = instaForm.elements.username.value; 
const comment = instaForm.elements.comment.value;
*/  

