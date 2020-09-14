const vm = new Vue({
    el: '#app',
    data: {
        message: {
            pid: 0,
            nickname: '',
            link: '',
            content: '',
            placeholder: '你想要对博主说什么呢(必填)...',
            current: 1,
            pages: 0,
            total: 0,
        },
        messages: [],
        friend: {
            current: 1,
            pages: 0,
            total: 0,
        },
        friends: [],
        loading: false,
    },
    methods: {
        getForm: function () {
            let item = localStorage.getItem("visitor");
            if (item != null) {
                let visitor = JSON.parse(item);
                this.message.nickname = visitor.nickname;
                this.message.link = visitor.link;
            }
        },
        clearForm: function () {
            this.message.nickname = '';
            this.message.link = '';
            this.message.content = '';
        },
        loadMore: function () {
            this.message.current += 1;
            this.getMessages();
        },
        reply: function (messageId, nickname) {
            this.message.placeholder = '@' + nickname;
            this.message.pid = messageId;
            this.clearForm();
            $.scrollTo('#message-area', 500);
            $('#veditor').focus();
        },
        validate: function () {
            if (this.message.content.trim() === '') {
                swal({
                    text: "留言内容不能为空",
                    icon: "error",
                    button: {
                        text: "确定",
                    },
                });
                return false;
            }
            if (this.message.link !== '') {
                let pattern = /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?/;
                if (!pattern.test(this.message.link)) {
                    swal({
                        text: "请填写完整地址（包含协议）",
                        icon: "error",
                        button: {
                            text: "确定",
                        },
                    });
                    return false;
                }
            }
            return true;
        },
        saveMessage: function () {
            if (!this.validate()) {
                return false;
            }
            let newMessage = {
                'pid': this.message.pid,
                'nickname': this.message.nickname,
                'link': this.message.link,
                'content': this.message.content,
            };
            this.loading = true;
            axios.post('/messages', newMessage,
                {
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest'
                    },
                })
                .then((result) => {
                    setTimeout(() => {
                        this.loading = false;
                        this.current = 1;
                        this.content = '';
                        this.getMessages();
                    }, 1000);
                    this.message.content = '';
                    $.scrollTo('.vinfo', 500);
                }).catch((error) => {
                Swal.fire({
                    icon: 'error',
                    title: '提交失败！',
                    text: `${error.response.data.message}`,
                });
            })
            return false;
        },
        getMessages: function () {
            axios({
                url: '/messages/?current=' + this.message.current,
                method: 'GET',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
            }).then((result) => {
                this.message.current = result.data.current;
                this.message.pages = result.data.pages;
                this.message.total = result.data.total;
                if (this.message.current <= 1) {
                    this.messages = result.data.records;
                } else {
                    this.messages = this.messages.concat(result.data.records);
                }
            })
        },
        getLinks: function () {
            axios({
                url: '/links/?current=' + this.friend.current,
                method: 'GET',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
            }).then((result) => {
                this.friend.current = result.data.current;
                this.friend.pages = result.data.pages;
                this.friend.total = result.data.total;
                if (this.friend.current <= 1) {
                    this.friends = result.data.records;
                } else {
                    this.friends = this.friends.concat(result.data.records);
                }
            })
        }
    },
    created() {
        this.getForm();
        this.getMessages();
        this.getLinks();
    },
    filters: {
        dateFormat: function (value) {
            return moment(value).fromNow();
        }
    },
});