$(document).ready(function() {
    // Initialize the map for each card when the page loads
    $('[id^=map-]').each(function() {
        var mapContainer = $(this)[0]; // The map container for the current card
        var mapOptions = {
            center: new kakao.maps.LatLng(33.450701, 126.570667), // Default center coordinates
            level: 3 // Default zoom level
        };
        var map = new kakao.maps.Map(mapContainer, mapOptions);
        var geocoder = new kakao.maps.services.Geocoder();
        var marker = new kakao.maps.Marker({
            map: map
        });

        // Get the address from the card and show the map
        var address = $(this).closest('.card').find('span[id^=address-]').text();
        geocoder.addressSearch(address, function(result, status) {
            if (status === kakao.maps.services.Status.OK) {
                var coords = new kakao.maps.LatLng(result[0].y, result[0].x);
                marker.setPosition(coords);
                map.setCenter(coords);
            } else {
                console.error('Geocoder failed due to:', status);
            }
        });
    });

    // Address search button click event
    $('#searchAddress').click(function(event) {
        event.preventDefault(); // 폼이 자동으로 제출되지 않도록 합니다.

        var query = $('#restaurantName').val().trim(); // 사용자가 입력한 식당 이름을 쿼리로 사용

        if (!query) {
            alert("식당 이름을 입력해주세요.");
            return;
        }

        // 이전 검색 결과 초기화
        $('#addressList').empty();

        // 네이버 지역 검색 API 호출
        $.ajax({
            url: '/naver/search', // 네이버 API 엔드포인트
            type: 'GET',
            data: {
                query: query
            },
            success: function(data) {
                var addressList = $('#addressList');

                if (data && data.items && Array.isArray(data.items)) {
                    // 검색 결과 반복 처리
                    data.items.forEach(function(item) {
                        var addressItem = $('<div></div>')
                            .text(item.address) // 주소 텍스트 표시
                            .attr('data-address', item.address) // 주소를 데이터 속성으로 저장
                            .addClass('address-item') // 스타일링을 위한 클래스 추가
                            .css({
                                cursor: 'pointer',
                                padding: '5px',
                                border: '1px solid #ddd',
                                margin: '5px 0'
                            });

                        // 주소를 클릭하면 선택된 주소를 입력 필드에 설정
                        addressItem.click(function() {
                            var selectedAddress = $(this).attr('data-address');
                            $('#address').val(selectedAddress); // 선택된 주소를 입력 필드에 설정

                            // 주소로 좌표를 검색합니다
                            geocoder.addressSearch(selectedAddress, function(result, status) {
                                if (status === kakao.maps.services.Status.OK) {
                                    var coords = new kakao.maps.LatLng(result[0].y, result[0].x);

                                    // 결과값으로 받은 위치를 마커로 표시합니다
                                    marker.setPosition(coords);
                                    marker.setMap(map);

                                    // 지도의 중심을 결과값으로 받은 위치로 이동시킵니다
                                    map.setCenter(coords);
                                } else {
                                    console.error('Geocoder failed due to:', status);
                                }
                            });
                        });

                        // 주소 항목을 주소 목록 컨테이너에 추가
                        addressList.append(addressItem);
                    });
                } else {
                    console.error('API 응답 데이터 형식 오류:', data);
                    alert('주소 목록을 불러오는 데 문제가 발생했습니다.');
                }
            },
            error: function() {
                alert('주소를 불러오는 데 실패했습니다.');
            }
        });
    });
});
