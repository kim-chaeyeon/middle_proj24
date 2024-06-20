$(document).ready(function() {
    $('.increment-btn').on('click', function() {
        var friendId = $(this).data('id');
        $.ajax({
            url: '/friend/increment/' + friendId,
            type: 'POST',
            success: function(data) {
                // 참가자 수를 업데이트합니다.
                $('button[data-id="' + friendId + '"]').prev().text('인원수: ' + data.currentParticipants);
            },
            error: function() {
                alert('인원수 증가에 실패했습니다.');
            }
        });
    });
});