layui.use(['form', 'miniTab'], function () {
    var form = layui.form,
        layer = layui.layer,
        miniTab = layui.miniTab,
        $ = layui.$;

    //获取用户信息
    $.ajax({
        url: '/admin/user/' + localStorage.getItem("userId") + '/info',
        type: 'GET',
        success: function (result) {
            let user = result.data;
            $('.layui-form input[name="id"]').val(user.id);
            $('.layui-form input[name="username"]').val(user.username);
            $('.layui-form input[name="nickname"]').val(user.nickname);
            $('.layui-form input[name="email"]').val(user.email);
            form.render();
        }
    });

    //自定义验证规则
    form.verify({
        //验证昵称
        nickname: function (value) { //value：表单的值、item：表单的DOM对象
            if (value.length < 3 || value.length > 16) {
                return '昵称长度为3-16个字符'
            }
            if (!new RegExp("^[a-zA-Z0-9_\u4e00-\u9fa5\\s·]+$").test(value)) {
                return '昵称不能有特殊字符';
            }
            if (/(^\_)|(\__)|(\_+$)/.test(value)) {
                return '昵称首尾不能出现下划线\'_\'';
            }
            if (/^\d+\d+\d$/.test(value)) {
                return '昵称不能全为数字';
            }
        }
    });

    //监听提交
    form.on('submit(saveBtn)', function (data) {
        axios({
            method: 'put',
            url: '/admin/user/info',
            data: data.field,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            },
        }).then((response) => {
            Swal.fire({
                icon: 'success',
                title: '修改成功',
                showConfirmButton: false,
                timer: 1500,
                onClose: () => {
                    miniTab.deleteCurrentByIframe();
                }
            });
        }).catch((error) => {
            Swal.fire({
                icon: 'error',
                title: '修改失败！',
                text: ` ${error.response.data.message}`,
            });
        })
        return false;
    });

});