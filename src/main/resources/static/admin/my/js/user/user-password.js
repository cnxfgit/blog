layui.use(['form', 'miniTab'], function () {
    var form = layui.form,
        layer = layui.layer,
        miniTab = layui.miniTab,
        $ = layui.$;

    //表单验证
    form.verify({
        newPassword: function (value) {
            let pattern = /^[a-zA-Z0-9_-]{6,16}$/;
            if (!pattern.test(value)) {
                return '密码为6-16位（字母，数字，下划线，减号）的组合';
            }
            let oldPassword = $('input[name="oldPassword"]').val();
            if (value === oldPassword) {
                return '新旧密码一致';
            }
        },
        againPassword: function (value) {
            let newPassword = $('input[name="newPassword"]').val();
            if (value !== newPassword) {
                return '两次输入的密码不一致';
            }
        }
    });

    //监听提交
    form.on('submit(saveBtn)', function (data) {
        data.field.userId = localStorage.getItem("userId");
        axios({
            method: 'put',
            url: '/admin/user/password',
            data: data.field,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            },
        }).then((response) => {
            Swal.fire({
                icon: 'success',
                title: '修改成功，请重新登录',
                showConfirmButton: false,
                timer: 1500,
                onClose: () => {
                    axios({
                        method: 'post',
                        url: '/admin/logout',
                    }).then((response) => {
                        window.location = '/admin/login.html';
                    })
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