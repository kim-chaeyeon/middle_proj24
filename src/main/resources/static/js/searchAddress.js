$(document).ready(function() {
    $('#searchAddress').click(function() {
        var query = $('#restaurantName').val(); // 식당 이름을 검색어로 사용

        // 네이버 지역 검색 API 호출
        $.ajax({
            url: '/naver/search',
            type: 'GET',
            data: {
                query: query
            },
            success: function(data) {
                // API 호출 성공 시 처리
                var address = data.items[0].address; // 첫 번째 검색 결과의 주소를 가져옴
                $('#address').val(address); // 주소 입력 필드에 주소 설정
            },
            error: function() {
                // API 호출 실패 시 처리
                alert('주소 검색에 실패했습니다.');
            }
        });
    });
});
