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
}
let curYear = 0, curMonth = 0;
const app = new Vue({
    el: '#app',
    data: {
        calendarData: [],
        current: 1,
        pages: 1,
        articles: [],
    },
    methods: {
        getCalendarData: function () {
            axios({
                url: '/archives',
                method: 'GET',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
            }).then((result) => {
                let archivesData = result.data;
                this.calendarData = archivesData.articleDates.map(item => {
                    return [item.date, item.articleCount];
                });
                let pageInfo = archivesData.pageInfo;
                this.articles = pageInfo.records;
                this.current = pageInfo.current;
                this.pages = pageInfo.pages;
                this.$nextTick(function () {
                    this.initCalendar(this.calendarData);
                    lazyLoadInstance.update();
                })
            })
        },
        toPage: function (page) {
            if (page >= 1 && page <= this.pages) {
                this.current = page;
                this.getArchives(page);
                $(window).scrollTo('#cd-timeline', 1000);
            }
        },
        initCalendar: function (data) {
            let myChart = echarts.init(document.getElementById('post-calendar'));
            let dayTime = 3600 * 24 * 1000;
            let endTime = +echarts.number.parseDate(new Date());
            let startTime = +(endTime - 365 * dayTime);
            let startDate = new Date(startTime).format('yyyy-MM-dd');
            let endDate = new Date(endTime).format('yyyy-MM-dd');
            function getData() {
                var date = data.map(item => item[0]);
                var res = [];
                for (var time = startTime; time <= endTime; time += dayTime) {
                    let formatTime = echarts.format.formatTime('yyyy-MM-dd', time);
                    let count = 0;
                    let index = date.indexOf(formatTime);
                    if (index !== -1) {
                        count = data[index][1];
                    }
                    res.push([formatTime, count]);
                }
                return res;
            }
            let option = {
                title: {
                    top: 0,
                    text: '文章日历',
                    left: 'center',
                    textStyle: {
                        color: '#3C4858'
                    }
                },
                tooltip: {
                    padding: 10,
                    backgroundColor: '#555',
                    borderColor: '#777',
                    borderWidth: 1,
                    formatter: function (obj) {
                        var value = obj.value;
                        return '<div style="font-size: 14px;">' + value[0] + '：' + value[1] + '</div>';
                    }
                },
                visualMap: {
                    show: true,
                    showLabel: true,
                    categories: [0, 1, 2, 3, 4],
                    calculable: true,
                    inRange: {
                        symbol: 'rect',
                        color: ['#ebedf0', '#c6e48b', '#7bc96f', '#239a3b', '#196127']
                    },
                    itemWidth: 12,
                    itemHeight: 12,
                    orient: 'horizontal',
                    left: 'center',
                    bottom: 0
                },
                calendar: [{
                    left: 'center',
                    range: [startDate, endDate],
                    cellSize: [14, 14],
                    splitLine: {
                        show: false
                    },
                    itemStyle: {
                        color: '#196127',
                        borderColor: '#fff',
                        borderWidth: 2
                    },
                    yearLabel: {
                        show: true,
                    },
                    monthLabel: {
                        nameMap: 'cn',
                        fontSize: 11
                    },
                    dayLabel: {
                        formatter: '{start}  1st',
                        nameMap: 'cn',
                        fontSize: 11
                    }
                }],
                series: [{
                    type: 'heatmap',
                    coordinateSystem: 'calendar',
                    calendarIndex: 0,
                    data: getData(),
                }]
            };
            myChart.setOption(option);
        },
        getArchives: function (current) {
            axios({
                url: '/archives-articles',
                params: {
                    current: current,
                },
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
                method: 'GET'
            }).then((result) => {
                this.articles = result.data.records;
                this.current = result.data.current;
                this.pages = result.data.pages;
                curYear = 0, curMonth = 0;
                this.$nextTick(function () {
                    lazyLoadInstance.update();
                });
            })
        },
        getSpecific: function (date, type) {
            date = this.$options.filters['dateFormat'](date);
            let arr = date.split('-');
            if (type === 'y') {
                return arr[0];
            } else if (type === 'm') {
                return arr[1];
            } else if (type === 'd') {
                return arr[2];
            }
        },
        isShow: function (time, type) {
            let date = this.$options.filters['dateFormat'](time);
            if (type === 'y') {
                let year = this.getSpecific(date, type);
                if (year !== curYear) {
                    curYear = year;
                    return true;
                }
                return false;
            } else if (type === 'm') {
                let month = this.getSpecific(date, type);
                if (month !== curMonth) {
                    curMonth = month;
                    return true;
                }
                return false;
            }
        },
    },
    created() {
        this.getCalendarData();
    },
    filters: {
        dateFormat: function (value) {
            return moment(value).format("YYYY-MM-DD");
        }
    },
});