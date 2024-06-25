const vm = new Vue({
    el: '#app',
    data() {
        return {
            room_name: '',
            chatrooms: [],
            nickname: '',
            creatorId: '' // 현재 사용자 ID 저장
        };
    },
    created() {
        this.findAllRoom();
        this.fetchCurrentUser();
    },
    methods: {
        fetchCurrentUser() {
            axios.get('/member/current')
                .then(response => {
                    this.nickname = response.data.nickname;
                    this.creatorId = response.data.id; // 사용자 ID 저장
                    localStorage.setItem('wschat.nickname', this.nickname);
                })
                .catch(error => {
                    console.error('Error fetching current user:', error);
                });
        },
        findAllRoom() {
            axios.get('/chat/api/rooms').then(response => {
                this.chatrooms = response.data;
            }).catch(error => {
                console.error('Failed to load chat rooms:', error);
                alert("Failed to load chat rooms.");
            });
        },
        createRoom() {
            if (!this.room_name) {
                alert("방 제목을 입력해 주십시오.");
                return;
            }
            const params = new URLSearchParams();
            params.append("name", this.room_name);
            params.append("creatorId", this.creatorId); // 생성자 ID 추가
            axios.post('/chat/room', params).then(response => {
                alert(`${response.data.name} 방이 성공적으로 생성되었습니다.`);
                this.room_name = '';
                this.findAllRoom();
            }).catch(error => {
                console.error('채팅방을 생성하는 데 실패했습니다:', error);
                alert("채팅방을 생성하는 데 실패했습니다.");
            });
        },
        enterRoom(roomId) {
            if (!this.nickname) {
                alert('로그인이 필요합니다.');
                return;
            }
            localStorage.setItem('wschat.sender', this.nickname);
            localStorage.setItem('wschat.roomId', roomId);
            window.location.href = `/chat/room/enter/${roomId}`;
        },
        deleteRoom(roomId) {
            if (confirm("정말로 이 채팅방을 삭제하시겠습니까?")) {
                axios.delete(`/chat/room/${roomId}`, { params: { nickname: this.nickname } }).then(response => {
                    alert("채팅방이 성공적으로 삭제되었습니다.");
                    this.findAllRoom();
                }).catch(error => {
                    console.error('채팅방을 삭제하는 데 실패했습니다:', error);
                    alert("회원님이 만든 채팅방만 삭제 할 수 있습니다.");
                });
            }
        },
        fetchUsersInRoom(roomId) {
            axios.get(`/chat/room/${roomId}/members`).then(response => {
                this.users = response.data;
            }).catch(error => {
                console.error('Error fetching users in room:', error);
            });
        }
    }
});
