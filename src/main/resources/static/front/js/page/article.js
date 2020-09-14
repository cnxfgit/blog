const vm = new Vue({
    el: '#app',
    data: {
        articleId: articleId,
        comment: {
            pid: 0,
            nickname: '',
            content: '',
            placeholder: '你想要对博主说什么呢(必填)...',
            current: 1,
            pages: 0,
            total: 0,
        },
        comments: [],
        loading: false,
    },
    methods: {
        getForm: function () {
            let item = localStorage.getItem("visitor");
            if (item != null) {
                let visitor = JSON.parse(item);
                this.comment.nickname = visitor.nickname;
            }
        },
        clearForm: function () {
            this.comment.nickname = '';
            this.comment.content = '';
        },
        loadMore: function () {
            this.comment.current += 1;
            this.getComments();
        },
        reply: function (commentId, nickname) {
            this.comment.placeholder = '@' + nickname;
            this.comment.pid = commentId;
            this.clearForm();
            $.scrollTo('#comment-area', 500);
            $('#veditor').focus();
        },
        likeIt: function () {
            axios({
                url: '/article/' + this.articleId + '/likes',
                method: 'PUT',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
            }).then((result) => {
                $('#likeBtn').addClass('disabled');
            })
            return false;
        },
        validate: function () {
            if (this.comment.content.trim() === '') {
                swal({
                    text: "评论内容不能为空",
                    icon: "error",
                    button: {
                        text: "确定",
                    },
                });
                return false;
            }
            return true;
        },
        saveComment: function () {
            if (!this.validate()) {
                return false;
            }
            let newComment = {
                'pid': this.comment.pid,
                'nickname': this.comment.nickname,
                'content': this.comment.content,
                'aid':this.articleId,
            };
            this.loading = true;
            axios.post('/comments', newComment,
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
                        this.getComments();
                    }, 1000);
                    this.comment.content = '';
                    $.scrollTo('.vinfo', 500);
                }).catch((error) => {
                Swal.fire({
                    icon: 'error',
                    title: '提交失败！',
                    text: `${error.response.data.comment}`,
                });
            })
            return false;
        },
        getComments: function () {
            axios({
                url: '/comments/listByArticleId/' + this.articleId + '?current=' + this.comment.current,
                method: 'GET',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
            }).then((result) => {
                this.comment.current = result.data.current;
                this.comment.pages = result.data.pages;
                this.comment.total = result.data.total;
                if (this.comment.current <= 1) {
                    this.comments = result.data.records;
                } else {
                    this.comments = this.comments.concat(result.data.records);
                }
            })
        },
        likeIt: function () {
            axios({
                url: '/article/' + this.articleId + '/likes',
                method: 'PUT',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
            }).then((result) => {
                $('#likeBtn').addClass('disabled');
            });
            return false;
        },
    },
    created() {
        this.getForm();
        this.getComments();
    },
    filters: {
        dateFormat: function (value) {
            return moment(value).fromNow();
        }
    },
});