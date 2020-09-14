layui.use(['layer', 'miniTab', 'echarts'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        miniTab = layui.miniTab,
        echarts = layui.echarts;
    const vm = new Vue({
        el: '#app',
        data: {
            articleCount: 0,
            categoryCount: 0,
            tagCount: 0,
            commentCount: 0,
            viewCount: 0,
            messageCount: 0,
            visitorCount: 0,
            userCount: 0,
            accessLogs: [],
            operationLogs: [],
            comments: [],
            messages: [],
            articles: [],
            frontViews: [],
            backViews: [],
            increasedViews: 0,
            increasedArticles: 0,
            increasedMessages: 0,
            increasedComments: 0,
            notices: [],
        },
        methods: {
            getData: function () {
                axios({
                    url: '/admin/indexData',
                    method: 'GET',
                }).then((result) => {
                    let data = result.data;
                    this.articleCount = data.articleCount;
                    this.categoryCount = data.categoryCount;
                    this.tagCount = data.tagCount;
                    this.commentCount = data.commentCount;
                    this.viewCount = data.viewCount;
                    this.messageCount = data.messageCount;
                    this.visitorCount = data.visitorCount;
                    this.userCount = data.userCount;
                    this.accessLogs = data.accessLogs;
                    this.operationLogs = data.operationLogs;
                    this.comments = data.comments;
                    this.messages = data.messages;
                    this.articles = data.articles;
                    this.frontViews = data.frontViews;
                    this.backViews = data.backViews;
                    this.increasedViews = data.increasedViews;
                    this.increasedArticles = data.increasedArticles;
                    this.increasedMessages = data.increasedMessages;
                    this.increasedComments = data.increasedComments;
                    this.notices = data.notices;
                    this.$nextTick(function () {
                        this.initChart();
                    })
                });
            },
            setLogClass(value) {
                return value === 1 ? 'layui-bg-green' : 'layui-bg-red';
            },
            setAuditClass(value) {
                if (value === 0) {
                    return 'layui-bg-red';
                } else if (value === 1) {
                    return 'layui-bg-blue';
                } else if (value === 2) {
                    return 'layui-bg-green';
                }
            },
            setAuditText(value) {
                if (value === 0) {
                    return '审核未过';
                } else if (value === 1) {
                    return '等待审核';
                } else if (value === 2) {
                    return '审核通过';
                }
            },
            toLocaleDate(value) {
                return moment(value).format('YYYY年M月D日');
            },
            getLast7Dates() {
                let start = moment().startOf('day').subtract(7, 'days');
                let dates = [];
                for (let i = 0; i <= 7; i++) {
                    let date = start.format('YYYY-MM-DD');
                    dates.push(date);
                    start = start.add(1, 'days');
                }
                return dates;
            },
            filterViewData(viewData) {
                let dateResult = this.getLast7Dates();
                let dates = viewData.map(item => item.date);
                let dataResult = [];
                $.each(dateResult, function (i, item) {
                    let index = dates.indexOf(item);
                    let count = 0;
                    if (index !== -1) {
                        count = viewData[index].viewCount;
                    }
                    dataResult.push(count);
                });
                return {
                    date: dateResult,
                    data: dataResult,
                }
            },
            initChart() {
                let frontViewsData = this.filterViewData(this.frontViews);
                let backViewsData = this.filterViewData(this.backViews);
                var echartsRecords = echarts.init(document.getElementById('echarts-records'), 'walden');
                var optionRecords = {
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {
                        data: ['最近7天前台流量', '最近7天后台流量']
                    },
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    toolbox: {
                        feature: {
                            saveAsImage: {}
                        }
                    },
                    xAxis: {
                        type: 'category',
                        boundaryGap: false,
                        data: frontViewsData.date,
                    },
                    yAxis: {
                        type: 'value'
                    },
                    series: [
                        {
                            name: '最近7天前台流量',
                            type: 'line',
                            data: frontViewsData.data,
                        },
                        {
                            name: '最近7天后台流量',
                            type: 'line',
                            data: backViewsData.data,
                        },
                    ]
                };
                echartsRecords.setOption(optionRecords);

                // echarts 窗口缩放自适应
                window.onresize = function () {
                    echartsRecords.resize();
                }
            }
        },
        created() {
            this.getData();
        },
        filters: {
            dateFormat: function (value) {
                return moment(value).fromNow();
            }
        },
    });

    miniTab.listen();
    /**
     * 查看公告信息
     **/
    $('body').on('click', '.layuimini-notice', function () {
        var title = $(this).children('.layuimini-notice-title').text(),
            noticeTime = $(this).children('.layuimini-notice-extra').text(),
            content = $(this).children('.layuimini-notice-content').html();
        var html = '<div style="padding:15px 20px; text-align:justify; line-height: 22px;border-bottom:1px solid #e2e2e2;background-color: #2f4056;color: #ffffff">\n' +
            '<div style="text-align: center;margin-bottom: 20px;font-weight: bold;border-bottom:1px solid #718fb5;padding-bottom: 5px"><h4 class="text-danger">' + title + '</h4></div>\n' +
            '<div style="font-size: 12px">' + content + '</div>\n' +
            '</div>\n';
        parent.layer.open({
            type: 1,
            title: '系统公告' + '<span style="float: right;right: 1px;font-size: 12px;color: #b1b3b9;margin-top: 1px">' + noticeTime + '</span>',
            area: '300px;',
            shade: 0.8,
            id: 'layuimini-notice',
            btn: ['取消'],
            btnAlign: 'c',
            moveType: 1,
            content: html,
        });
    });

    checkPermission();
});