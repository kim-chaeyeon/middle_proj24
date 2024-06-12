const vm = new Vue({
    el: '#app',
    data() {
        return {
            room_name: '',
            chatrooms: [],
            username: ''
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
                    this.username = response.data.username; // Assuming the username field is what you need
                    localStorage.setItem('wschat.username', this.username); // Storing username in local storage
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
            if (!this.username) {
                alert('로그인이 필요합니다.');  // Prompt user to log in if username isn't set
                return;
            }
            localStorage.setItem('wschat.sender', this.username);  // Store the username in local storage
            localStorage.setItem('wschat.roomId', roomId);  // Store the room ID in local storage
            window.location.href = `/chat/room/enter/${roomId}`;  // Redirect to the room entry URL
        }
    }
});