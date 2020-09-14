let app = new Vue({
    el: '#app',
    data: {
        categories: [],
        colors: ['#D5F5E3', '#E8F8F5', '#82E0AA', '#A3E4D7',
            '#FEF9E7', '#F9E79F', '#F8C471', '#F9EBEA'],
        articles: [],
        current: 1,
        pages: 1,
        categoryId: null,
    },
    methods: {
        getCategories: function () {
            axios({
                url: '/categories',
                method: 'GET',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
            }).then((result) => {
                this.categories = result.data;
                this.$nextTick(function () {
                    this.initRandomChart(this.categories);
                })
            })
        },
        initRandomChart: function (data) {
            let radarChart = echarts.init(document.getElementById('category-radar'));
            let indicator = [];
            let value = [];
            let maxValue = data.sort((a, b) => {
                return b.articleCount - a.articleCount;
            })[0].articleCount;
            $.each(data, function (index, cate) {
                if (cate.articleCount > maxValue) {
                    maxValue = cate.articleCount;
                }
                let category = {};
                category.name = cate.name;
                category.max = maxValue;
                indicator.push(category);
                value.push(cate.articleCount);
            });

            let option = {
                title: {
                    left: 'center',
                    text: '文章分类雷达图',
                    textStyle: {
                        fontWeight: 500,
                        fontSize: 22
                    }
                },
                tooltip: {},
                radar: {
                    name: {
                        textStyle: {
                            color: '#3C4858'
                        }
                    },
                    max: 8,
                    indicator: indicator,
                    nameGap: 5,
                    center: ['50%', '55%'],
                    radius: '66%'
                },
                series: [{
                    type: 'radar',
                    color: ['#3ecf8e'],
                    itemStyle: {normal: {areaStyle: {type: 'default'}}},
                    data: [
                        {
                            value: value,
                            name: '文章分类数量'
                        }
                    ]
                }]
            };
            radarChart.setOption(option);
        },
        getArticlesByCategoryId: function (categoryId, current) {
            axios({
                url: '/category/' + categoryId + '/articles?current=' + current,
                method: 'GET',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
            }).then((result) => {
                this.articles = result.data.records;
                this.current = result.data.current;
                this.pages = result.data.pages;
                this.categoryId = categoryId;
                this.$nextTick(function () {
                    lazyLoadInstance.update();
                });
            })
        },
        toPage: function (page) {
            if (page >= 1 && page <= this.pages) {
                this.current = page;
                this.getArticlesByCategoryId(this.categoryId, page);
                $(window).scrollTo('#articleCards', 1000);
            }
        },
        getQueryString: function (name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
            var r = window.location.search.substr(1).match(reg);  //匹配目标参数
            if (r != null) return decodeURI(r[2]);
            return null;
        },
    },
    created: function () {
        this.getCategories();
        let cid = this.getQueryString('id');
        if (cid !== null) {
            this.categoryId = cid;
            this.getArticlesByCategoryId(this.categoryId, 1);
        }
    },
    filters: {
        dateFormat: function (value) {
            return moment(value).format("YYYY-MM-DD");
        }
    },
});