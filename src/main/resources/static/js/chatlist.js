const vm = new Vue({
    el: '#app',
    data() {
        return {
            room_name: '',
            chatrooms: [],
            nickname: ''
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
                    this.nickname = response.data.nickname; // Assuming the response contains the nickname field
                    localStorage.setItem('wschat.nickname', this.nickname); // Storing the nickname in local storage
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
                alert('로그인이 필요합니다.');  // Prompt user to log in if username isn't set
                return;
            }
            localStorage.setItem('wschat.sender', this.nickname);  // Store the username in local storage
            localStorage.setItem('wschat.roomId', roomId);  // Store the room ID in local storage
            window.location.href = `/chat/room/enter/${roomId}`;  // Redirect to the room entry URL
        },
        deleteRoom(roomId) {
            if (confirm("정말로 이 채팅방을 삭제하시겠습니까?")) {
                axios.delete(`/chat/room/${roomId}`).then(response => {
                    alert("채팅방이 성공적으로 삭제되었습니다.");
                    this.findAllRoom();
                }).catch(error => {
                    console.error('채팅방을 삭제하는 데 실패했습니다:', error);
                    alert("채팅방을 삭제하는 데 실패했습니다.");
                });
            }
        },
        fetchUsersInRoom: function() {
            var vm = this;
            axios.get(`/chat/room/${this.roomId}/users`).then(function(response) {
                vm.users = response.data;
            }).catch(function(error) {
                console.error('Error fetching users in room:', error);
            });
        }
    }
});
