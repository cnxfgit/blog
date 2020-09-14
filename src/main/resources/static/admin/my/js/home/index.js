layui.use(['jquery', 'layer', 'miniAdmin'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        miniAdmin = layui.miniAdmin;

    var options = {
        iniUrl: "/admin/init",    // 初始化接口
        urlHashLocation: true,      // 是否打开hash定位
        bgColorDefault: 0,          // 主题默认配置
        multiModule: true,          // 是否开启多模块
        menuChildOpen: true,       // 是否默认展开菜单
        loadingTime: 0,             // 初始化加载时间
        pageAnim: true,             // iframe窗口动画
        maxTabNum: 20,              // 最大的tab打开数量
    };
    miniAdmin.render(options);

    var username = localStorage.getItem("username");
    $('#username').text(username);

    $('.login-out').on("click", function () {
        axios({
            method: 'post',
            url: '/admin/logout',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        }).then((response) => {
            Swal.fire({
                toast: true,
                position: 'center',
                icon: 'success',
                title: '退出登录成功!',
                showConfirmButton: false,
                timer: 1000,
                timerProgressBar: true,
                onClose: () => {
                    window.location = '/admin/login.html';
                }
            });
        });
    });
});