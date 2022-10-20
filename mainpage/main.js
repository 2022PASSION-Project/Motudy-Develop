/*변수(toggleBtn, menu, login)를 선언하고
querySelector를 이용, css에서 해당 class를 가진 요소를 찾고 
변수에 넣었음*/
const toggleBtn = document.querySelector('.navbar_toggleBtn');
const menu = document.querySelector('.navbar_menu');
const login = document.querySelector('.navbar_login');


/*토글 버튼 클릭시 함수호출
함수는 위에서 선언한 변수 menu, login의 classlist에 
'active'클래스를 토글링 해줍니다.
즉, 토글버튼을 클릭 할 때 마다 menu와 login에
'active'클래스가 없으면 넣어주고 있으면 빼줍니다.
*/
toggleBtn.addEventListener('click',() => {
    menu.classList.toggle('active');
    login.classList.toggle('active');
});