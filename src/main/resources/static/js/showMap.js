function showMap(address, mapId) {
    var geocoder = new kakao.maps.services.Geocoder();

    // 주소로 좌표를 검색합니다
    geocoder.addressSearch(address, function(result, status) {
        // 정상적으로 검색이 완료됐으면
        if (status === kakao.maps.services.Status.OK) {
            var coords = new kakao.maps.LatLng(result[0].y, result[0].x);

            // 지도를 표시할 div와 지도 옵션을 설정합니다
            var container = document.getElementById(mapId);
            if (!container) {
                console.error('Map container not found:', mapId);
                return;
            }

            var options = {
                center: coords,
                level: 3
            };

            // 지도를 생성합니다
            var map = new kakao.maps.Map(container, options);

            // 마커를 생성하고 지도에 표시합니다
            var marker = new kakao.maps.Marker({
                position: coords
            });
            marker.setMap(map);
        } else {
            console.error('Geocoder failed due to:', status);
        }
    });
}