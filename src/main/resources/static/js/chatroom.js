var vm = new Vue({
    el: '#app',
    data: function() {
        return {
            roomId: '',
            room: {},
            sender: '',
            message: '',
            photo: null,
            messages: [],
            members: [], // 멤버 객체 리스트
            ws: null,
            reconnectAttempts: 0
        };
    },
    created: function() {
        this.roomId = localStorage.getItem('wschat.roomId');
        this.sender = localStorage.getItem('wschat.nickname');
        if (!this.roomId || !this.sender) {
            console.error("Valid room ID or sender not found in localStorage. Found:", this.roomId, this.sender);
            this.fetchCurrentUser();
            return;
        }
        this.messages = JSON.parse(localStorage.getItem(`messages_${this.roomId}`)) || [];
        this.initializeWebSocket();
        this.findRoom();
        this.fetchMembers();
    },
    methods: {
        fetchCurrentUser: function() {
            axios.get('/member/current')
                .then(response => {
                    this.sender = response.data.nickname;
                    localStorage.setItem('wschat.nickname', this.sender);
                })
                .catch(error => {
                    console.error('Error fetching current user:', error);
                });
        },
        initializeWebSocket: function() {
            var sock = new SockJS("/ws-stomp");
            this.ws = Stomp.over(sock);
            this.connect();
        },
        connect: function() {
            var vm = this;
            this.ws.connect({}, function(frame) {
                vm.ws.subscribe(`/sub/chat/room/${vm.roomId}`, function(message) {
                    var recv = JSON.parse(message.body);
                    vm.recvMessage(recv);
                });
                vm.ws.send(`/pub/chat/message/${vm.roomId}`, {}, JSON.stringify({
                    type: 'ENTER',
                    roomId: vm.roomId,
                    sender: vm.sender,
                    timestamp: new Date().toISOString()
                }));
                vm.addMember(vm.sender); // Add the current user to the members list on the client side
                axios.post(`/chat/room/${vm.roomId}/enter`, null, { params: { nickname: vm.sender } })
                    .then(() => vm.fetchMembers()) // Add the current user to the members list on the server side
                    .catch(error => console.error('Error entering room:', error));
            }, function(error) {
                if (vm.reconnectAttempts++ < 5) {
                    setTimeout(vm.connect, 10000);
                } else {
                    console.error("Failed to reconnect after 5 attempts.");
                }
            });
        },
        findRoom: function() {
            var vm = this;
            axios.get(`/chat/room/${vm.roomId}`).then(function(response) {
                vm.room = response.data;
            }).catch(function(error) {
                console.error('Error fetching room details:', error);
            });
        },
        fetchMembers: function() {
            var vm = this;
            axios.get(`/chat/room/${vm.roomId}/members`).then(function(response) {
                vm.members = response.data.map(member => {
                    if (member.thumbnailImg) {
                        // 이미지 경로가 올바른지 확인하고 설정합니다.
                        member.thumbnailImg = `/file/${member.thumbnailImg}`;
                    } else {
                        member.thumbnailImg = 'default-profile-image.png';
                    }
                    return member;
                });
            }).catch(function(error) {
                console.error('Error fetching room members:', error);
            });
        },
        addMember: function(nickname) {
            if (!this.members.some(member => member.nickname === nickname)) {
                this.members.push({ nickname: nickname, thumbnailImg: null });
            }
        },
        removeMember: function(nickname) {
            this.members = this.members.filter(member => member.nickname !== nickname);
        },
        sendMessage: function() {
            if (!this.message.trim() && !this.photo) return;
            const messagePayload = {
                type: this.photo ? 'PHOTO' : 'TALK',
                roomId: this.roomId,
                sender: this.sender,
                timestamp: new Date().toISOString()
            };
            if (this.photo) {
                var formData = new FormData();
                formData.append('file', this.photo);
                axios.post('/upload', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                }).then(response => {
                    messagePayload.photoUrl = response.data;
                    this.ws.send(`/pub/chat/message/${this.roomId}`, {}, JSON.stringify(messagePayload));
                    this.photo = null;
                    this.message = '';
                    this.scrollToBottom(); // 전송 후 스크롤 이동
                }).catch(error => {
                    console.error('Error uploading photo:', error);
                });
            } else {
                messagePayload.message = this.message.trim();
                this.ws.send(`/pub/chat/message/${this.roomId}`, {}, JSON.stringify(messagePayload));
                this.message = '';
                this.scrollToBottom(); // 전송 후 스크롤 이동
            }
        },
        recvMessage: function(recv) {
            const key = `entered_${this.roomId}_${recv.sender}`;
            if (recv.type === 'ENTER' && !localStorage.getItem(key)) {
                localStorage.setItem(key, 'true');
                this.messages.push({
                    type: recv.type,
                    sender: '[알림]',
                    message: `${recv.sender}님이 방에 들어왔습니다.`,
                    timestamp: recv.timestamp
                });
                this.addMember(recv.sender);
            } else if (recv.type === 'PHOTO') {
                this.messages.push({
                    type: recv.type,
                    sender: recv.sender,
                    photoUrl: recv.photoUrl,
                    timestamp: recv.timestamp
                });
            } else if (recv.type !== 'ENTER') {
                this.messages.push({
                    type: recv.type,
                    sender: recv.sender,
                    message: recv.message,
                    timestamp: recv.timestamp
                });
            }
            this.$nextTick(() => {
                this.scrollToBottom(); // 수신 후 스크롤 이동
            });
            localStorage.setItem(`messages_${this.roomId}`, JSON.stringify(this.messages));
        },
        scrollToBottom: function() {
            var container = this.$el.querySelector('.messages');
            if (container) {
                container.scrollTop = container.scrollHeight;
            }
        },
        handlePhotoChange: function(event) {
            const file = event.target.files[0];
            this.photo = file;
            if (file) {
                this.message = `Selected file: ${file.name}`;
            }
        },
        handleKeyPress: function(event) {
            if (event.key === 'Enter') {
                this.sendMessage();
            }
        },
        handleMemberClick: function(member) {
            if (member.nickname === this.sender) {
                window.location.href = "/member/myPage";
            } else {
                window.location.href = `/member/myPage/${member.nickname}`;
            }
        }
    }
});
