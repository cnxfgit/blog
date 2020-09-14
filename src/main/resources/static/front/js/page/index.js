const vm = new Vue({
    el: '#app',
    data: {
        topArticles: [],
        recommendArticles: [],
        articles: [],
        current: 1,
        pages: 1,
    },
    methods: {
        getHomeArticles: function () {
            axios({
                url: '/home',
                method: 'GET',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
            }).then((result) => {
                let homeArticles = result.data;
                this.topArticles = homeArticles.topArticles;
                this.recommendArticles = homeArticles.recommendArticles;
                let pageInfo = homeArticles.pageInfo;
                this.articles = pageInfo.records;
                this.current = pageInfo.current;
                this.pages = pageInfo.pages;
                this.$nextTick(function () {
                    this.initCarousel();
                    lazyLoadInstance.update();
                });
            })
        },
        getArticles: function (current) {
            axios({
                url: '/articles?current=' + current,
                method: 'GET',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
            }).then((result) => {
                this.articles = result.data.records;
                this.current = result.data.current;
                this.pages = result.data.pages;
                this.$nextTick(function () {
                    lazyLoadInstance.update();
                });
            })
        },
        toPage: function (page) {
            if (page >= 1 && page <= this.pages) {
                this.current = page;
                this.getArticles(page);
                $(window).scrollTo('#articleCards', 1000);
            }
        },
        initCarousel: function () {
            // TODO 此处的轮播尚需优化，PC/Mobile 在触摸切换时都感觉相当不灵活
            $(function () {
                let coverSlider = $('.carousel');
                //用户触摸轮播自动 restartPlay 是否生效
                let initUserPressedOrDraggedActive = false
                //用户触摸轮播自动 restartPlay
                function initUserPressedOrDragged(instance) {
                    setInterval(() => {
                        if (instance.pressed || instance.dragged) {
                            // console.log('initUserPressedOrDragged: ',instance.pressed,instance.dragged)
                            restartPlay()
                        }
                    }, 1000)
                }
                coverSlider.carousel({
                    duration: Number('120'),
                    fullWidth: true,
                    indicators: 'true' === 'true',
                    onCycleTo() {
                        if (!initUserPressedOrDraggedActive) {
                            // console.log('initUserPressedOrDraggedActive')
                            initUserPressedOrDragged(this)
                            initUserPressedOrDraggedActive = true
                        }
                    },
                })
                let carouselIntervalId;
                // Loop to call the next cover article picture.
                let autoCarousel = function () {
                    carouselIntervalId = setInterval(function () {
                        coverSlider.carousel('next');
                    }, 10000);
                };
                autoCarousel();
                function restartPlay() {
                    clearInterval(carouselIntervalId);
                    autoCarousel();
                };
                // prev and next cover post.
                $('#prev-cover').click(function () {
                    coverSlider.carousel('prev');
                    restartPlay();
                });
                $('#next-cover').click(function () {
                    coverSlider.carousel('next');
                    restartPlay();
                });
            });
        }
    },
    created() {
        this.getHomeArticles();
    },
    filters: {
        dateFormat: function (value) {
            return moment(value).format("YYYY-MM-DD");
        }
    },
});