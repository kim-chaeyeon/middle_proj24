 function checkLogin() {
        var loggedIn = true; // 실제로는 서버에서 로그인 상태를 확인하는 로직이 필요합니다.
        if (!loggedIn) {
            window.location.href = "/member/login"; // 로그인 페이지의 URL로 리다이렉트합니다.
        }
    }

    window.onload = checkLogin;

body {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    margin: 0;
    background-color: #f0f0f0;
    font-family: Arial, sans-serif;
}
.container {
    width: 90%;
    max-width: 1200px;
    background-color: #ffffff;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    box-sizing: border-box;
}
h1 {
    text-align: center;
    margin-bottom: 20px;
    font-size: 32px;
    color: #FF4081;
}
.profile-img {
    display: block;
    width: 150px;
    height: 150px;
    border-radius: 50%;
    margin: 0 auto 20px;
    object-fit: cover;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.info-container {
    display: flex;
    flex-wrap: wrap;
    justify-content: center; /* 중앙 정렬을 위한 설정 */
}
.info-box {
    background-color: #f9f9f9;
    padding: 15px;
    border-radius: 5px;
    margin: 10px; /* 박스 간 간격을 위한 설정 */
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    display: flex;
    align-items: center;
    justify-content: flex-start;
    width: 45%; /* 박스의 너비를 45%로 설정하여 두 줄에 배치 */
}
.info-box i {
    color: #FFCE45;
    margin-right: 10px;
}
.info-box span {
    font-size: 16px;
    color: #555;
}
.main-btn {
    display: inline-block;
    margin: 10px;
    padding: 10px 20px;
    background-color: #FFCE45;
    color: #333;
    text-decoration: none;
    border-radius: 5px;
    font-weight: bold;
    transition: background-color 0.3s;
}
.main-btn:hover {
    background-color: #FFD700;
}

.sub-btn {
    display: inline-block;
    margin: 10px; /* 여백 설정 */
    padding: 10px 20px;
    background-color: #FFCE45;
    color: gray;
    text-decoration: none;
    border-radius: 5px;
    font-weight: bold;
    transition: background-color 0.3s;
    margin-left: auto; /* 오른쪽 정렬 */
}
.sub-btn:hover {
    background-color: #FFD700;
}