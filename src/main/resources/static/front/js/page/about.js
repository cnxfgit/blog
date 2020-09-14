const app = new Vue({
    el: '#app',
    data: {
        articleCount: 0,
        categoryCount: 0,
        tagCount: 0,
        categories: [],
        tags: [],
        articleDates: [],
    },
    methods: {
        getAboutData: function () {
            axios({
                url: '/about',
                params: {
                    dateType: 2,
                },
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
                method: 'GET'
            }).then((result) => {
                let aboutData = result.data;
                this.articleCount = aboutData.articleCount;
                this.categoryCount = aboutData.categoryCount;
                this.tagCount = aboutData.tagCount;
                this.aboutData = aboutData.aboutData;
                this.categories = aboutData.categories;
                this.tags = aboutData.tags;
                this.articleDates = aboutData.articleDates;

                this.$nextTick(function () {
                    this.initCharts();
                    lazyLoadInstance.update();
                })
            })
        },
        initCharts: function () {
            this.initPostsChart(this.articleDates);
            this.initCategoriesChart(this.categories);
            this.initTagsChart(this.tags);
        },
        initPostsChart: function (data) {
            Date.prototype.format = function (fmt) {
                var o = {
                    "M+": this.getMonth() + 1, //月份
                    "d+": this.getDate(), //日
                    "h+": this.getHours(), //小时
                    "m+": this.getMinutes(), //分
                    "s+": this.getSeconds(), //秒
                    "q+": Math.floor((this.getMonth() + 3) / 3), //季度
                    "S": this.getMilliseconds() //毫秒
                };
                if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
                for (var k in o)
                    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
                return fmt;
            };

            function getNextMonth(date) {
                var arr = date.split('-');
                var year = arr[0]; //获取当前日期的年份
                var month = arr[1]; //获取当前日期的月份
                var day = arr[2]; //获取当前日期的日
                var days = new Date(year, month, 0);
                days = days.getDate(); //获取当前日期中的月的天数
                var year2 = year;
                var month2 = parseInt(month) + 1;
                if (month2 == 13) {
                    year2 = parseInt(year2) + 1;
                    month2 = 1;
                }
                var day2 = day;
                var days2 = new Date(year2, month2, 0);
                days2 = days2.getDate();
                if (day2 > days2) {
                    day2 = days2;
                }
                if (month2 < 10) {
                    month2 = '0' + month2;
                }

                var t2 = year2 + '-' + month2 + '-' + day2;
                return t2;
            };

            let postsChart = echarts.init(document.getElementById('posts-chart'));

            function getPostData(data) {
                let dayTime = 3600 * 24 * 1000;
                let endTime = +echarts.number.parseDate(new Date());
                let startTime = +(endTime - 365 * dayTime);
                let startDate = new Date(startTime).format('yyyy-MM-dd');
                let date = data.map(item => item.date);
                let totalDate = [];
                let totalCount = [];

                for (let cur = startDate, i = 1; i <= 13; cur = getNextMonth(cur), i++) {
                    let formatDate = cur.substring(0, cur.lastIndexOf('-'));
                    totalDate.push(formatDate);
                    let count = 0;
                    let index = date.indexOf(formatDate);
                    if (index !== -1) {
                        count = data[index].articleCount;
                    }
                    totalCount.push(count);
                }
                return [totalDate, totalCount];
            };

            let postData = getPostData(data);
            let dateValue = postData[0], dataValue = postData[1];
            let postsOption = {
                title: {
                    text: '文章发布统计图',
                    top: -5,
                    x: 'center'
                },
                tooltip: {
                    trigger: 'axis'
                },
                xAxis: {
                    type: 'category',
                    data: dateValue,
                },
                yAxis: {
                    type: 'value',
                },
                series: [
                    {
                        name: '文章篇数',
                        type: 'line',
                        color: ['#6772e5'],
                        data: dataValue,
                        markPoint: {
                            symbolSize: 45,
                            color: ['#fa755a', '#3ecf8e', '#82d3f4'],
                            data: [{
                                type: 'max',
                                itemStyle: {color: ['#3ecf8e']},
                                name: '最大值'
                            }, {
                                type: 'min',
                                itemStyle: {color: ['#fa755a']},
                                name: '最小值'
                            }]
                        },
                        markLine: {
                            itemStyle: {color: ['#ab47bc']},
                            data: [
                                {type: 'average', name: '平均值'}
                            ]
                        }
                    }
                ]
            };
            postsChart.setOption(postsOption);
        },
        initCategoriesChart: function (data) {
            let categoriesChart = echarts.init(document.getElementById('categories-chart'));
            data = data.map(item => {
                return {
                    name: item.name,
                    value: item.articleCount
                }
            })
            let categoriesOption = {
                title: {
                    text: '文章分类统计图',
                    top: -4,
                    x: 'center'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                series: [
                    {
                        name: '分类',
                        type: 'pie',
                        radius: '50%',
                        color: ['#6772e5', '#ff9e0f', '#fa755a', '#3ecf8e', '#82d3f4', '#ab47bc', '#525f7f', '#f51c47', '#26A69A'],
                        data: data,
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    }
                ]
            };
            categoriesChart.setOption(categoriesOption);
        },
        initTagsChart: function (data) {
            let tagsChart = echarts.init(document.getElementById('tags-chart'));

            let names = data.map(item => {
                return item.name;
            });
            let values = data.map(item => {
                return item.articleCount;
            })

            let tagsOption = {
                title: {
                    text: 'TOP10 标签统计图',
                    top: -5,
                    x: 'center'
                },
                tooltip: {},
                xAxis: [
                    {
                        type: 'category',
                        data: names,
                    }
                ],
                yAxis: [
                    {
                        type: 'value'
                    }
                ],
                series: [
                    {
                        type: 'bar',
                        color: ['#82d3f4'],
                        barWidth: 18,
                        data: values,
                        markPoint: {
                            symbolSize: 45,
                            data: [{
                                type: 'max',
                                itemStyle: {color: ['#3ecf8e']},
                                name: '最大值'
                            }, {
                                type: 'min',
                                itemStyle: {color: ['#fa755a']},
                                name: '最小值'
                            }],
                        },
                        markLine: {
                            itemStyle: {color: ['#ab47bc']},
                            data: [
                                {type: 'average', name: '平均值'}
                            ]
                        }
                    }
                ]
            };
            tagsChart.setOption(tagsOption);
        }
    },
    created() {
        this.getAboutData();
    },
});
