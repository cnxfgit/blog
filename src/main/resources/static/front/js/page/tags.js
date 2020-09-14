let app = new Vue({
    el: '#app',
    data: {
        tags: [],
        colors: ['#D5F5E3', '#E8F8F5', '#82E0AA', '#A3E4D7',
            '#FEF9E7', '#F9E79F', '#F8C471', '#F9EBEA'],
        articles: [],
        current: 1,
        pages: 1,
        tagId: null,
    },
    methods: {
        getTags: function () {
            axios({
                url: '/tags',
                method: 'GET',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
            }).then((result) => {
                this.tags = result.data;
                this.getWordArray();
            })
        },
        getArticlesByTagId: function (tagId, current) {
            axios({
                url: '/tag/' + tagId + '/articles?current=' + current,
                method: 'GET',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
            }).then((result) => {
                this.articles = result.data.records;
                this.current = result.data.current;
                this.pages = result.data.pages;
                this.tagId = tagId;
                this.$nextTick(function () {
                    lazyLoadInstance.update();
                });
            })
        },
        toPage: function (page) {
            if (page >= 1 && page <= this.pages) {
                this.current = page;
                this.getArticlesByTagId(this.tagId, page);
                $(window).scrollTo('#articleCards', 1000);
            }
        },
        getWordArray: function () {
            let wordArray = [];
            $.each(this.tags, function (index, tag) {
                let word = {};
                word.text = tag.name;
                word.weight = tag.articleCount;
                word.link = 'javascript:;';
                wordArray.push(word);
            });
            $("#tag-wordcloud").jQCloud(wordArray, {autoResize: true});
        },
        getQueryString: function (name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
            var r = window.location.search.substr(1).match(reg);  //匹配目标参数
            if (r != null) return decodeURI(r[2]);
            return null;
        }
    },
    created: function () {
        this.getTags();
        let tid = this.getQueryString('id');
        if (tid !== null) {
            this.tagId = tid;
            this.getArticlesByTagId(this.tagId, 1);
        }
    },
    filters: {
        dateFormat: function (value) {
            return moment(value).format("YYYY-MM-DD");
        }
    },
});